package nlp;

import static nlp.Utils.getFullFileText;

import java.io.File;
import java.io.FileInputStream;
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

/**
 * @author Nick Brixius (brixiusn@erau.edu)
 * @author Sean Holden (holdens@my.erau.edu)
 *
 */
public class CountOccurrenceOfWords {

	/** List of files to scan */
	private static File[] fileList;

	public static void main(String[] args) throws IOException {		

		if (args.length == 0){
			// Someone didn't use the command line.  Oh well, we'll invoke the GUI
			//			fileList = Utils.getFileViaGui();
			fileList = new File[1];
			fileList[0] = new File("data/text.txt");
		}
		else{
			// Try to get the filename from the command line
			fileList = new File[1];
			fileList[0] = new File(args[0]);
		}

		for(File f : fileList){
			scanFile(f);
		}


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
	 * <li> From within the Word class, calculate the <i>normalized</i> frequency by comparing the sample frequency woth the COCA frequency
	 * <li> Print the results
	 * </ol>
	 * @param text The text to scan
	 * @throws IOException
	 */
	public static void scanText(String text){
	
		/** Uncleansed list of the words parsed from the text */
		String[] words;
		
		/** Map of all of the words in the provided string (key) and the number of occurrences (value) */
		TreeMap<String, Integer> map = new TreeMap<String, Integer>();
		/** Map of all of the words in the COCA Academic texts (key) and the number of occurrences (value) */
		TreeMap<String, Integer> cocaMap = new TreeMap<String, Integer>();
		/** Excel File from which to populate the COCA map */
		final File cocaFile = new File("data/allWords.xls");	// TODO learn to use XSSF for xlsx (or not...)
		
		System.out.println("Beginning scan...");
		
		// Split the text into words based on the regex
		words = text.split("[ \n\t\r.,;:!?(){}]");
		
		// Clean the array of words and load the words into the map
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

		// Get all entries into a set
		Set<Map.Entry<String, Integer>> entrySet = map.entrySet();


		try {
			// POI jazz to get the first sheet fron teh Excel file
			POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(cocaFile));
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
					String word = row.getCell(3).getStringCellValue();

					// If the word is in the map, get the freq value and add it to the map
					if (map.containsKey(word)){
						cocaMap.put(word, (int)row.getCell(6).getNumericCellValue());
						// Print it for funzies
//		        		System.out.printf("%s\t\t%d\n", word, (int)row.getCell(6).getNumericCellValue());
					}

				}
			}
		} catch(Exception ioe) {
			ioe.printStackTrace();
		}

//		System.out.println("======================");


//		System.out.printf("Occurrence of all %d words in %s:\n", entrySet.size(), f.getName());

		// Create word list
		List<Word> wordlist = new ArrayList<Word>(entrySet.size());

		// Get key and value from each entry
		for (Map.Entry<String, Integer> entry: entrySet){
			//			System.out.println(entry.getValue() + "\t" + entry.getKey());

			// Ignore really infrequent words
			if (entry.getValue() > 4){
				try{
					wordlist.add(new Word(entry.getKey(), entry.getValue(), cocaMap.get(entry.getKey())));
				} catch (Exception e){
					// Do nothing
				}
			}
		}

		/** Sorts the wordlist based on the implementation of the compareTo() method in the Word class */ 
		wordlist.sort(null);

		// Print
//		System.out.printf("Occurrence of top %d words in %s:\n", wordlist.size(), f.getName());
		System.out.printf("Occurrence of %d words in the sample:\n", wordlist.size());
		System.out.println("Raw freq\tCOCA freq\tNorm freq\tWord");
		System.out.println("----------------------------------------");

		for (Word w : wordlist){
//			System.out.println(w.getRawFrequency() + "\t" + w.getValue());
			System.out.printf("%d\t\t%d\t\t%.2f\t\t%s\n",w.getRawFrequency(), w.getGlobalFrequency(), w.getNormalFreq(),  w.getValue());
		}

		System.out.println("------------------");
	}
}
