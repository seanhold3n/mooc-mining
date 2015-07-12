package edu.erau.holdens.moocmining;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

/** Map of all of the words in the COCA Academic texts (key) and the number of occurrences (value)
 * @author Sean Holden (holdens@my.erau.edu)
 *
 */
public class COCAMap extends HashMap<String, Integer>{

	private static final long serialVersionUID = -6394472852481588497L;
	
	/** Excel File from which to populate the COCA map */
	private static final File COCA_FILE = new File("data/allWords.xls");	

	/** The (ideally) single COCA map */
	private static COCAMap cocamap;
	
	/** Get the "official" COCA map object from this class */
	public static COCAMap getInstance(){
		if (cocamap == null){
			cocamap = new COCAMap();
		}
		return cocamap;
	}
	
	public static void populateCocaMap() throws IOException{
		populateCocaMapFromWords(null);
	}
	
	/**
	 * @param map A map containing the words for which to search
	 * @return A TreeMap of all of the words in the COCA sheet (key) and the occurrence of each word (value)
	 * @throws IOException
	 */
	public static void populateCocaMapFromWords(HashMap<String, Integer> map) throws IOException{
		
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
				if (
//						map.containsKey(word) && ( // TODO ignoring if it'll be used - get ALL THE WORDS!
						(pos.equals("n")		// Noun
						|| pos.equals("j")	// Adjective
						|| pos.equals("v")	// Verb
						|| pos.equals("r")	// Adverb						
				)){
					getInstance().put(word, (int)row.getCell(COL_COCA_ALL).getNumericCellValue());
				}

			}
		}
		
		System.out.println("Number of COCA words added: " + rows);

	}
	
}
