package edu.erau.holdens.moocmining;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/** Contains useful generic methods.
 * @author Sean Holden (holdens@my.erau.edu)
 *
 */
public class Utils {

	/** Imports the contents of a given file as a Java string.
	 * @param f The file to read
	 * @return The content of the file as a string
	 * @throws IOException For a variety of I/O reasons
	 */
	public static String getFullFileText(File f) throws IOException{
		// Create a buffered file reader
		BufferedReader bufferedReader = new BufferedReader(new FileReader(f));

		// Initialize the strings for reading
		String text = "", currentStr = "";

		// Read the file
		while((currentStr = bufferedReader.readLine()) != null){
			text += currentStr;
		}

		// Close the reader
		bufferedReader.close();
		
		return text;
	}
	
}
