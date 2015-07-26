package edu.erau.holdens.moocmining;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

/** Map of all of the words in the COCA Academic texts (key) and the number of occurrences (value).
 * @author Sean Holden (holdens@my.erau.edu)
 */
public class COCAMap extends HashMap<String, Integer>{

	private static final long serialVersionUID = -6394472852481588497L;
	
	/** Path to the Excel file (.xls only; .xlsx will NOT work) from which to populate the COCA map. */
	public static final String COCA_FILE_PATH = "data/allWords.xls";	

	/** The (ideally) single COCA map. */
	private static COCAMap cocamap;
	
	/** Get the "official" COCA map object from this class.
	 * @return A COCAMap of all of the words in the COCA sheet (key) and the occurrence of each word (value).
	 */
	public static COCAMap getInstance(){
		if (cocamap == null){
			cocamap = new COCAMap();
		}
		return cocamap;
	}
	
	/** Populates the COCA map object in this class (accessible via the {@link getInstance} method) with all of the words in
	 * the COCA file.  The location of this file is specified by {@link COCAMap#COCA_FILE_PATH}.
	 * @throws IOException If an error occurs while reading the file
	 */
	public static void populateCocaMap() throws IOException{
		
		/** The column in the sheet containing the words */
		final int COL_WORD = 3;		
		/** The column in the sheet containing the parts of speech (PoS) */
		final int COL_POS = 4;		
		/** The column in the sheet containing the word count in all COCA entries */
		final int COL_COCA_ALL = 5;
		/** The column in the sheet containing the word count in all COCA Academic entries */
		@SuppressWarnings("unused")
		final int COL_COCA_ACAD = 6;

		// POI jazz to get the first sheet from the Excel file
		POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(new File(COCA_FILE_PATH)));
		HSSFWorkbook wb = new HSSFWorkbook(fs);
		HSSFSheet sheet = wb.getSheetAt(0);
		HSSFRow row;

		// Get the number of rows in the sheet
		int rows = sheet.getPhysicalNumberOfRows();

		int cols = 0; // Number of columns
		int tmp = 0;

		// This trick ensures that we get the data properly even if it doesn't start from first few rows
		// TODO I'm not sure why this is here or if it's really needed
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
				
				// If the word is in the map, get the freq value and add it to the map.
				// This only applies to nouns, adjectives, verbs, and adverbs.
				if (
						(pos.equals("n")	// Noun
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
