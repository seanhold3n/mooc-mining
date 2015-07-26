package edu.erau.holdens.moocmining;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/**
 * @author Sean Holden (holdens@my.erau.edu), with some derivative code (original word counting code) from Nick Brixius (brixiusn@erau.edu)
 */
public class MainStuff {

	/** Excel File from which to populate the discussion text */
	private static final File DISCUSSIONS_FILE = new File("data/transcripts.xls");
	/** Excel File from which to populate the discussion data (entry author, learning phase) */
	private static final File DISCUSSIONS_DATA_FILE = new File("data/data.10.29.2014.xls");
	/** Plaintext file containing all of the discussion text without regard to entry number */
	private static final File ALL_TEXT_FILE = new File("data/text.txt");
	/** CSV file containing the word-count vectors for each discussion entry */
	public static final File STRUCTURED_FILE = new File("data/structuredData.csv");
	/** ARFF file containing the word-count vectors for each discussion entry */
	public static final File ARFF_FILE = new File("data/structuredData.arff");
	/** The number of top words to get for analysis ("top" words when comparing normalized frequency values) */
	public static final int N_TOP_WORDS = 40;

	// TODO learn to use XSSF for xlsx (or not...)

	public static void main(String[] args) throws Exception {

		// Load the discussions
		System.out.println("Populating discussions map...");
		HashMap<Integer, DiscussionEntry> discussions = createTranscriptsMap();


		// Populate COCA map
		System.out.println("Populating COCA map...");
		COCAMap.populateCocaMap();


		// Scan the file of all discussions
		System.out.println("Getting word frequencies for all words in all discussions...");
		List<Word> wordlist = getWordFrequencyList(ALL_TEXT_FILE);


		/** Sorts the wordlist based on the implementation of the compareTo() method in the Word class.
		 * Note: This method requires Java 8 or higher */ 
		System.out.println("Sorting wordlist based on normalized frequency...");
		wordlist.sort(null); // TODO look into NavigableMap and NavigableSet for this


		// Trim the wordlist
		System.out.println("Generating populatity of top " + N_TOP_WORDS + " words in all discussions...");
		wordlist = wordlist.subList(0, N_TOP_WORDS);


		/* Write the ARFF file.
		 * Note: There are methods in the Weka API to do this, but I fancy doing it more manually.
		 * (see: https://weka.wikispaces.com/Creating+an+ARFF+file and https://weka.wikispaces.com/Programmatic+Use ) */
		// Open the writer
		BufferedWriter arffWriter = new BufferedWriter(new FileWriter(ARFF_FILE));

		// Write the header
		arffWriter.write("@RELATION mooc\n\n");

		// Write the word attributes
		for (Word w : wordlist){
			// The ternary operator for "class" is to seperate the word class from the actual class of data defined later
			arffWriter.write(String.format("@ATTRIBUTE %s\tinteger\n", (w.getValue().equals("class") ? "class_" : w.getValue())));
		}

		// Write the classes
		arffWriter.write("@ATTRIBUTE class\t{T,E,I,R}\n\n");

		// Write the data only for known classes
		arffWriter.write("@DATA\n");
		for (DiscussionEntry d : discussions.values()){

			// Do not include unknowns in the ARFF
			if (d.getLearningPhase() != LearningPhase.X){

				HashMap<String, Integer> wordMap = d.scanWithRespectTo(wordlist);
				// Write word data
				for (int count : wordMap.values()){
					arffWriter.write(String.format("%d,", count));
				}
				// Write class
				arffWriter.write(d.getLearningPhase().name() + "\n");
			}
		}


		// Close the writer
		arffWriter.close();

		System.out.println("Results printed to " + ARFF_FILE.getAbsolutePath());

	}


	/**
	 * @return A TreeMap containing key-value pairs of the words entry number (key) and the discussion entry (value)
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static HashMap<Integer, DiscussionEntry> createTranscriptsMap() throws FileNotFoundException, IOException{

		/** The column in the sheet containing the encoded names (data xls) */
		final int COL_NAME = 0;
		/** The column in the sheet containing the words (text xls) */
		final int COL_TEXT = 1;
		/** The number of rows to analyze in the data workbook */
		final int DATA_ROWS = 746; 	// TODO gets wonky when this is 747
		/** The number of rows to analyze in the data workbook */
		final int DISCUSSION_ROWS = 1230;

		HashMap<Integer, DiscussionEntry> map = new HashMap<Integer, DiscussionEntry>();

		// POI jazz to get the first sheet from the Excel file
		HSSFSheet discussionSheet = new HSSFWorkbook(new FileInputStream(DISCUSSIONS_FILE)).getSheetAt(0);
		HSSFSheet dataSheet = new HSSFWorkbook(new FileInputStream(DISCUSSIONS_DATA_FILE)).getSheetAt(0);

		// DiscussionEntry data
		String name;
		String text;
		LearningPhase lp;

		/** Flag for the inner while loop to see if we are still reading rows for a single
		 * discussion entry */
		boolean stillReading = false;

		int discRowNum = 1;

		// Get the first row of text per entry
		HSSFRow textRow = dataSheet.getRow(discRowNum);

		// Get all of the data (exclude the header row)
		for (int dataRowNum = 1; dataRowNum < DATA_ROWS; dataRowNum++){

			HSSFRow thisRow = dataSheet.getRow(dataRowNum);

			// Get the author
			name = thisRow.getCell(COL_NAME).getStringCellValue().trim();

			// Get the learning phase
			if (checkCell(thisRow.getCell(2))){
				lp = LearningPhase.T;
			}
			else if (checkCell(thisRow.getCell(3))){
				lp = LearningPhase.E;
			}
			else if (checkCell(thisRow.getCell(4))){
				lp = LearningPhase.I;
			}
			else if (checkCell(thisRow.getCell(5))){
				lp = LearningPhase.R;
			}
			else{
				lp = LearningPhase.X;
			}

			// Reset text
			text = "";
			stillReading = true;

			/* Get the text from the other workbook.
			 * A while loop is used because an unknown number of rows
			 * contain information for one discussion entry.
			 */
			while (stillReading){
				// Get the text from the row
				try{
					text += textRow.getCell(COL_TEXT).getStringCellValue()+"\n";
				} catch (Exception e){};

				// Get the next row
				discRowNum++;
				textRow = discussionSheet.getRow(discRowNum);
				try{
					stillReading = textRow.getCell(0).getStringCellValue().equals("");
				}catch (NullPointerException npe){
					stillReading = true;
				}
			}

			map.put(dataRowNum, new DiscussionEntry(dataRowNum, name, text, lp));

		}

		assert(discRowNum == DISCUSSION_ROWS);

		return map;

	}


	/**
	 * @param text The string containing the words to count.
	 * @return A TreeMap containing key-value pairs of the words (key) and the number of occurrences (value)
	 */
	public static HashMap<String, Integer> getWordCounts(String text){

		// Split the text into words based on this regex
		String[] words = text.split("[ \n\t\r.,;:!?(){}]");

		// Create the map to store the words
		HashMap<String, Integer> map = new HashMap<String, Integer>();

		for (int i = 0; i < words.length; i++) {

			// Tidy up the word slightly
			String key = words[i].toLowerCase().trim();

			if (words[i].length() >= 1 && Character.isLetter(words[i].charAt(0)) ) {

				// If the word doesn't exist in the map, add it
				if (map.get(key) == null) {
					map.put(key, 1);
				}
				// If it does exist, increment the value 
				else {
					int value = map.get(key).intValue();
					value++;
					map.put(key, value);
				}
			}
		}

		return map;
	}


	/** Invokes the {@link #getWordFrequencyList(String)} method on the complete text of the given file.
	 * @param f The file to scan
	 * @throws IOException If there is an error getting the file text
	 */
	public static List<Word> getWordFrequencyList(File f) throws IOException{
		return getWordFrequencyList(Utils.getFullFileText(f));
	}


	/** Scans a string.  Any output is currently written to the console.
	 * The general procedure of this method is as follows:
	 * <ol>
	 * <li> Create a map of all of the words and their frequencies in the given string
	 * <li> Merge the data of that map and the {@link COCAMap} to create a list of {@link Word} objects that contains values for both the frequencies.  
	 * Note: this assumes that the COCAMap has already been populated using {@link COCAMap#populateCocaMap()}
	 * <li> From within the Word class, calculate the <i>normalized</i> frequency by comparing the sample frequency with the COCA frequency
	 * </ol>
	 * @param text The text to scan
	 */
	public static List<Word> getWordFrequencyList(String text) {

		/** Map of all of the words in the provided string (key) and the number of occurrences (value) */
		HashMap<String, Integer> map;

		// Clean the array of words and load the words into the map
		map = getWordCounts(text);

		// Get all entries into a set
		Set<Map.Entry<String, Integer>> entrySet = map.entrySet();

		// Create word list
		List<Word> wordlist = new ArrayList<Word>(entrySet.size());

		// Get key and value from each entry
		for (Map.Entry<String, Integer> entry: entrySet){
			try{
				// Create a Word object and add it to the wordlist
				wordlist.add(new Word(entry.getKey(), entry.getValue(), COCAMap.getInstance().get(entry.getKey())));
			} catch (Exception e){} // Do nothing if an error occurs
		}

		return wordlist;	

	}

	/** Helper method to see if the value of the cell is a 1.  This is used when determining the
	 * learning phase of a post.
	 * @param c The cell to check
	 * @return <b>true</b> if the cell contains a numeric 1 value, or <br>
	 * <b>false</b> if it doesn't or if an error occurs (when checking a 
	 * null cell, for example)
	 */
	private static boolean checkCell(HSSFCell c){
		try{
			return (c.getNumericCellValue() == 1);
		} catch (NullPointerException npe){
			return false;
		}		
	}


}
