package edu.erau.holdens.moocmining.sandbox;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.TreeMap;

import edu.erau.holdens.moocmining.DiscussionEntry;
import edu.erau.holdens.moocmining.MainStuff;

public class ExcelReadSandbox {

	public static void main(String[] args) throws FileNotFoundException, IOException {

//		// TODO learn to use XSSF for xlsx (or not...)
//		File file = new File("data/allWords.xls");
//		
//		try {
//		    POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(file));
//		    HSSFWorkbook wb = new HSSFWorkbook(fs);
//		    HSSFSheet sheet = wb.getSheetAt(0);
//		    HSSFRow row;
//		    HSSFCell cell;
//
//		    int rows; // No of rows
//		    rows = sheet.getPhysicalNumberOfRows();
//
//		    int cols = 0; // No of columns
//		    int tmp = 0;
//
//		    // This trick ensures that we get the data properly even if it doesn't start from first few rows
//		    for(int i = 0; i < 10 || i < rows; i++) {
//		        row = sheet.getRow(i);
//		        if(row != null) {
//		            tmp = sheet.getRow(i).getPhysicalNumberOfCells();
//		            if(tmp > cols) cols = tmp;
//		        }
//		    }
//
//		    for(int r = 0; r < rows; r++) {
//		        row = sheet.getRow(r);
//		        if(row != null) {
//		            for(int c = 0; c < cols; c++) {
//		                cell = row.getCell(c);
//		                if(cell != null) {
//		                    System.out.println(cell.getStringCellValue());
//		                }
//		            }
//		        }
//		    }
//		} catch(Exception ioe) {
//		    ioe.printStackTrace();
//		}
		
		TreeMap<Integer, DiscussionEntry> map = MainStuff.createTranscriptsMap();
		for (Entry<Integer, DiscussionEntry> d : map.entrySet()){
			System.out.println(d.getValue().toString());
		}

	}

}
