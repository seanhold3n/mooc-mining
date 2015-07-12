package edu.erau.holdens.moocmining;

import static edu.erau.holdens.moocmining.Utils.getFullFileText;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.format.CellTextFormatter;
import org.apache.poi.ss.usermodel.Cell;

/**
 * @author Sean Holden (holdens@my.erau.edu), with some derivative code (original word counting code) from Nick Brixius (brixiusn@erau.edu)
 */
public class MainStuff {

	/** Excel File from which to populate the COCA map */
	private static final File COCA_FILE = new File("data/allWords.xls");	

	/** Excel File from which to populate the discussion text */
	private static final File DISCUSSIONS_FILE = new File("data/transcripts.xls");
	/** Excel File from which to populate the discussion data (entry author, learning phase) */
	private static final File DISCUSSIONS_DATA_FILE = new File("data/data.10.29.2014.xls");

	// TODO learn to use XSSF for xlsx (or not...)

	
	/** Map of all of the words in the COCA Academic texts (key) and the number of occurrences (value) */
	private static TreeMap<String, Integer> cocaMap;
	
	
	public static void main(String[] args) throws IOException {	
		// Initialize an empty COCA map
		cocaMap = new TreeMap<String, Integer>();
		
		// Scan stuff
		scanFile(new File("data/text.txt"));
	}

	
	/**
	 * @return A TreeMap containing key-value pairs of thewords entry number (key) and the discussion entry (value)
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static TreeMap<Integer, DiscussionEntry> createTranscriptsMap() throws FileNotFoundException, IOException{
		
		/** The column in the sheet containing the encoded names */
		final int COL_NAME = 0;
		/** The column in the sheet containing the words */
		final int COL_TEXT = 1;
		/** The number of rows to analyze in the data workbook */
		final int DATA_ROWS = 746; 	// TODO gets wonky when this is 747
		/** The number of rows to analyze in the data workbook */
		final int DISCUSSION_ROWS = 1230;
		
		TreeMap<Integer, DiscussionEntry> map = new TreeMap<Integer, DiscussionEntry>();
		
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
			
			// Get the author
			name = dataSheet.getRow(dataRowNum).getCell(COL_NAME).getStringCellValue();

			// Reset text
			text = "";
			stillReading = true;

			/* Get the text from the other workbook.
			 * A while loop is used because an unknown number of rows
			 * contain information for one discussion entry.
			 */
			while (stillReading){
				// Get the text from the row
				System.out.printf("Currently reading dataRow %d; discussionRow %d\n", dataRowNum, discRowNum);
//				text += textRow.getCell(COL_TEXT).getStringCellValue();
				try{
					text += textRow.getCell(COL_TEXT).getStringCellValue()+"\n";
					System.out.println(text);
				} catch (Exception e){System.err.println(e.getMessage());};
				
				// Get the next row
				discRowNum++;
				textRow = discussionSheet.getRow(discRowNum);
				try{
					stillReading = textRow.getCell(0).getStringCellValue().equals("");
				}catch (NullPointerException npe){
					stillReading = true;
				}
			}
			
			
			map.put(dataRowNum, new DiscussionEntry(dataRowNum, name, text, LearningPhase.X));
		
			
		}
		
		assert(discRowNum == DISCUSSION_ROWS);
		
		return map;
		
	}

	
	/**
	 * @param map A map containing the words for which to search
	 * @return A TreeMap of all of the words in the COCA sheet (key) and the occurrence of each word (value)
	 * @throws IOException
	 */
	public static void populateCocaMapFromWords(TreeMap<String, Integer> map) throws IOException{
		
		/** The column in the sheet containing the words */
		final int COL_WORD = 3;		
		/** The column in the sheet containing the parts of speech (PoS) */
		final int COL_POS = 4;		
		/** The column in the sheet containing the word count in all COCA entries */
		final int COL_COCA_ALL = 5;
		/** The column in the sheet containing the word count in all COCA Academic entries */
		final int COL_COCA_ACAD = 6;

		// POI jazz to get the first sheet from the Excel file
		POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(COCA_FILE));
		HSSFWorkbook wb = new HSSFWorkbook(fs);
		HSSFSheet sheet = wb.getSheetAt(0);
		HSSFRow row;

		// Get the number of rows in the sheet
		int rows = sheet.getPhysicalNumberOfRows();

		int cols = 0; // Number of columns
		int tmp = 0;

		// This trick ensures that we get the data properly even if it doesn't start from first few rows
		for(int i = 0; i < 10 || i < rows; i++) {
			row = sheet.getRow(i);
			if(row != null) {
				tmp = sheet.getRow(i).getPhysicalNumberOfCells();
				if(tmp > cols) cols = tmp;
			}
		}

		for(int r = 1; r < rows; r++) {
			row = sheet.getRow(r);
			if(row != null) {
				// Get the word from the row
				String word = row.getCell(COL_WORD).getStringCellValue();

				// Get the part of the speech of the word in the list
				String pos = row.getCell(COL_POS).getStringCellValue();
				
				// If the word is in the map, get the freq value and add it to the map
				if (map.containsKey(word) && (
						pos.equals("n")		// Noun
						|| pos.equals("j")	// Adjective
						|| pos.equals("v")	// Verb
						|| pos.equals("r")	// Adverb						
				)){
					cocaMap.put(word, (int)row.getCell(COL_COCA_ALL).getNumericCellValue());
				}

			}
		}

	}


	/**
	 * @param text The string containing the words to count.
	 * @return A TreeMap containing key-value pairs of the words (key) and the number of occurrences (value)
	 */
	public static TreeMap<String, Integer> getWordCounts(String text){

		// Split the text into words based on this regex
		String[] words = text.split("[ \n\t\r.,;:!?(){}]");

		// Create the map to store the words
		TreeMap<String, Integer> map = new TreeMap<String, Integer>();

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
	

	/** Scans a file.  Any output is currently written to the console.
	 * @param f The file to scan
	 * @throws IOException
	 */
	public static void scanFile(File f) throws IOException{
		scanText(getFullFileText(f));
	}

	
	/** Scans a string.  Any output is currently written to the console.
	 * The general procedure of this method is as follows:
	 * <ol>
	 * <li> Create a map of all of the words and their frequencies in the given string
	 * <li> Create a map of all matching words and their frequencies in the COCA Excel sheet
	 * <li> Merge the data of the two maps by creating a list of {@link Word} objects that contains values for both the frequencies
	 * <li> From within the Word class, calculate the <i>normalized</i> frequency by comparing the sample frequency with the COCA frequency
	 * <li> Print the results
	 * </ol>
	 * @param text The text to scan
	 * @throws IOException 
	 */
	public static void scanText(String text) throws IOException{


		/** Map of all of the words in the provided string (key) and the number of occurrences (value) */
		TreeMap<String, Integer> map;

		System.out.println("Beginning scan...");


		// Clean the array of words and load the words into the map
		map = getWordCounts(text);

		// Get all entries into a set
		Set<Map.Entry<String, Integer>> entrySet = map.entrySet();

		// Create word list
		List<Word> wordlist = new ArrayList<Word>(entrySet.size());

		// Populate the COCA map using words from the sample
		cocaMap.putAll(populateCocaMapFromWords(map));	// TODO untested

		// Get key and value from each entry
		for (Map.Entry<String, Integer> entry: entrySet){

			// Ignore really infrequent words
			if (entry.getValue() > 4){
				try{
					wordlist.add(new Word(entry.getKey(), entry.getValue(), cocaMap.get(entry.getKey())));
				} catch (Exception e){} // Do nothing if an error occurs
			}
		}

		/** Sorts the wordlist based on the implementation of the compareTo() method in the Word class.
		 * Note: This method requires Java 8 or higher */ 
		wordlist.sort(null); // TODO look into NavigableMap and NavigableSet for this

		// Print
		//		System.out.printf("Occurrence of top %d words in %s:\n", wordlist.size(), f.getName());
		System.out.printf("Occurrence of %d words in the sample:\n", wordlist.size());
		System.out.println("Raw freq\tCOCA freq\tNorm freq\tWord");
		System.out.println("----------------------------------------------------");

		for (Word w : wordlist){
			System.out.printf("%d\t\t%d\t\t%.2f\t\t%s\n", w.getRawFrequency(), w.getGlobalFrequency(), w.getNormalFreq(),  w.getValue());
		}

		System.out.println("----------------------------------------------------");
	}
	

}
