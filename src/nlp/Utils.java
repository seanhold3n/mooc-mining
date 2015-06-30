package nlp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

/** Contains useful generic methods.
 * @author Sean Holden (holdens@my.erau.edu)
 *
 */
public class Utils {

	/** Retrieves a list of files that the user selects from a GUI
	 * @return The list of files selected
	 * @author Sean Holden (holdens@my.erau.edu)
	 */
	// TODO JavaFX implementation
	public static File[] getFileViaGui(){
		final JFileChooser fc = new JFileChooser();

		fc.setFileFilter(new FileNameExtensionFilter("Java Source Files (*.java)", "java"));
		fc.setMultiSelectionEnabled(true);
		// StackOverflow helped me with this next line
		fc.setCurrentDirectory( new File(Utils.class.getClassLoader().getResource("").getPath()).getParentFile() );

		int returnVal = fc.showOpenDialog(null);

		if ((returnVal == JFileChooser.APPROVE_OPTION)){
			return fc.getSelectedFiles();
		}
		else{
			JOptionPane.showMessageDialog(null, "No file or invalid selection; exiting.");
			return null;
		}

	}
	
	/** Imports the contents of a given file as a Java string.
	 * @param f The file to read
	 * @return The content of the file as a string
	 * @throws IOException For a variety of IO reasons
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
