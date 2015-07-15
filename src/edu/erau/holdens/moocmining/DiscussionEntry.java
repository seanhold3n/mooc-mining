package edu.erau.holdens.moocmining;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class DiscussionEntry implements Comparable<DiscussionEntry> {
	
	/** The number of the entry in the discussion */
	private int number;
	/** The (encoded) name of the author of the entry */
	private String author;
	/** The text of the discussion */
	private String text;
	/** The phase of learning that describes this discussion item */
	private LearningPhase phase;
	
	/** Creates a new {@link DiscussionEntry} object.
	 * @param number The number of the entry in the discussion
	 * @param author The author of the entry
	 * @param text The text of the discussion
	 * @param phase The phase of learning that describes this discussion item
	 */
	public DiscussionEntry(int number, String author, String text, LearningPhase phase) {
		super();
		this.number = number;
		this.author = author;
		this.text = text;
		this.phase = phase;
	}
	
	
	public int compareTo(DiscussionEntry o) {
		return this.getEntryNumber() - o.getEntryNumber();
	}
	
	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}
	
	/**
	 * @return The author of this entry
	 */
	public String getAuthor() {
		return author;
	}

	/**
	 * @return The number of the entry in the discussion
	 */
	public int getEntryNumber() {
		return number;
	}
	
	/**
	 * @return The text of the discussion
	 */
	public String getDiscussionText() {
		return text;
	}
	
	/**
	 * @return The phase of learning that describes this discussion item 
	 */
	public LearningPhase getLearningPhase() {
		return phase;
	}
	
	
	// TODO clean up documentation - this was copy-pasted from scanText() in MainStuff 
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
	 */
	public void scan(){

		/** Map of all of the words in the provided string (key) and the number of occurrences (value) */
		HashMap<String, Integer> map;

//		System.out.println("Beginning scan...");


		// Clean the array of words and load the words into the map
		map = MainStuff.getWordCounts(text);

		// Get all entries into a set
		Set<Map.Entry<String, Integer>> entrySet = map.entrySet();

		// Create word list
		List<Word> wordlist = new ArrayList<Word>(entrySet.size());

		// Populate the COCA map using words from the sample
//		populateCocaMapFromWords(map);	// removing this added SOOO MUUUUCH SPEEEED!! :D

		// Get key and value from each entry
		for (Map.Entry<String, Integer> entry: entrySet){

			// Ignore really infrequent words
//			if (entry.getValue() > 4){
				try{
					wordlist.add(new Word(entry.getKey(), entry.getValue(), COCAMap.getInstance().get(entry.getKey())));
				} catch (Exception e){} // Do nothing if an error occurs
//			}
		}

		/** Sorts the wordlist based on the implementation of the compareTo() method in the Word class.
		 * Note: This method requires Java 8 or higher */ 
		wordlist.sort(null); // TODO look into NavigableMap and NavigableSet for this
		
		// Print 10 most frequent words based on normalized frequency
		StringBuilder printStr = new StringBuilder(String.format("Entry: %3d, Author: %3s, Phase: %s, words: {", number, author, phase.name()));
		for (int i = 0; i < 10; i++){
			try{
				printStr.append(wordlist.get(i) + (i==9 ? "}" : ", "));
			} catch (IndexOutOfBoundsException ioobe){
//				System.err.printf("Entry %3d vector compilation ended abruptly on index %d\n", number, i);
				printStr.append("<end>}");
				break;
			}
		}
		System.out.println(printStr);
		
	}
	
	/** Generates a word-count based on a list of given words; that is, this method will only return the raw frequency
	 * of words for words that are in the given wordlist
	 * @param wordlist The List of words with the respect of which to scan <sub>Phrasing that was fun...</sub> 
	 * @return A HashMap containing the words (key) and the raw frequency in this discussion entry (value)
	 */
	public HashMap<String, Integer> scanWithRespectTo(List<Word> wordlist){
		
		// Create a map of words from the wordlist with a default value (number of occurrences) of zero
		// Split the text into words based on this regex
		String[] words = text.split("[ \n\t\r.,;:!?(){}]");

		// Create the map to store the words
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		for (Word w : wordlist){
			map.put(w.getValue(), 0);
		}

		// Scan the discussion entry
		for (int i = 0; i < words.length; i++) {

			// Tidy up the word slightly
			String key = words[i].toLowerCase().trim();

			if (words[i].length() >= 1 && Character.isLetter(words[i].charAt(0)) ) {

				// Increment the value in the map only if the word is present
				if (map.get(key) != null) {
					int value = map.get(key).intValue();
					value++;
					map.put(key, value);
				}
			}
		}
		
		// Return the map
		return map;
		
	}
	
	public String toString(){
		return String.format("Number: %d; Author: %s; Phase: %s; Text: %s", number, author, phase.name(), text);
	}

}
