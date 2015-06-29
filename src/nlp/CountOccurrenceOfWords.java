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

	// File list
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
	
	public static void scanFile(File f) throws IOException{

		scanText(getFullFileText(f));
		
		
	}
	
	public static void scanText(String text){
		// Create a TreeMap to hold words as key and count as value
		TreeMap<String, Integer> map = new TreeMap<String, Integer>();

		String[] words = text.split("[ \n\t\r.,;:!?(){}]");

		for (int i = 0; i < words.length; i++) {

			String key = words[i].toLowerCase();
			key.trim();
			
			if (words[i].length() >= 1 && Character.isLetter(words[i].charAt(0)) ) {


				if (map.get(key) == null) {
					map.put(key, 1);
				}
				else {
					int value = map.get(key).intValue();
					value++;
					map.put(key, value);
				}
			}
		}

		// Get all entries into a set
		Set<Map.Entry<String, Integer>> entrySet = map.entrySet();
		
		// Create a TreeMap to hold words as key and count as value
		TreeMap<String, Integer> cocamap = new TreeMap<String, Integer>();

		
		// TODO learn to use XSSF for xlsx (or not...)
		File file = new File("data/allWords.xls");
		
		try {
		    POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(file));
		    HSSFWorkbook wb = new HSSFWorkbook(fs);
		    HSSFSheet sheet = wb.getSheetAt(0);
		    HSSFRow row;

		    int rows; // No of rows
		    rows = sheet.getPhysicalNumberOfRows();

		    int cols = 0; // No of columns
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
		        		cocamap.put(word, (int)row.getCell(6).getNumericCellValue());
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
				wordlist.add(new Word(entry.getKey(), entry.getValue(), cocamap.get(entry.getKey())));
				} catch (Exception e){
					// Do nothing
				}
			}
		}
		
		// Sort
		wordlist.sort(null);
		
		// Print
//		System.out.printf("Occurrence of top %d words in %s:\n", wordlist.size(), f.getName());
		System.out.printf("Occurrence of top %d words:\n", wordlist.size());
		System.out.println("Norm freq\tRaw freq\tWord");
		System.out.println("----------------------------------------");

		for (Word w : wordlist){
//			System.out.println(w.getRawFrequency() + "\t" + w.getValue());
			System.out.printf("%.2f\t\t%d\t\t%s\n", w.getNormalFreq(), w.getRawFrequency(), w.getValue());
		}
		
		System.out.println("------------------");
	}
}
