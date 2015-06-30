package nlp;

/**
 * @author Sean Holden (holdens@my.erau.edu)
 */
public class Word implements Comparable<Word>{
	
	/** Determines the sort order of used in {@link #compareTo(Word)}.  <code>true</code>
	 * will sort the list from low to high; <code>false</code> will sort high to low.  */
	private static final boolean SORT_ASCENDING = false;
	
	/** A value to arbitrarily (but uniformly) modify the result of 
	 * <code>({@link #rawFrequency} / {@link #globalFrequency})</code> 
	 * for a higher distance between data points. */
	private static final int NORM_MOD = 1000;
	
	
	/** The frequency of the word in COCA */
	private final int globalFrequency;
	
	/** The frequency of the word in the sample with respect to the global frequency */
	private final double normalFreq;
	
	/** The frequency of the word in the sample */
	private final int rawFrequency;
	
	/** The string (word) represented by this Word object */
	private String value;
		
	
	/** Creates a new word object.
	 * @param value The word itself
	 * @param rawFrequency The frequency of the word in the sample
	 * @param globalFrequency The frequency of the word in COCA
	 */
	public Word(String value, int rawFrequency, int globalFrequency) {
		super();
		this.value = value;
		this.rawFrequency = rawFrequency;
		this.globalFrequency = globalFrequency;
		this.normalFreq = (rawFrequency*1.0 / globalFrequency) * NORM_MOD;
	}
	
	
	/** Compares two words based on their normalized frequency.  This method uses the
	 * {@link #SORT_ASCENDING} (currently {@value #SORT_ASCENDING}) to determine
	 * the sort order.
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Word o){
		// Sort by raw frequency
//		return (SORT_ASCENDING) ? this.getRawFrequency() - o.getRawFrequency() 
//								: o.getRawFrequency() - this.getRawFrequency();
		
		// Sort by normalized frequency
		return (SORT_ASCENDING) ? (int)((this.getNormalFreq() - o.getNormalFreq())*NORM_MOD)
								: (int)(( o.getNormalFreq() - this.getNormalFreq())*NORM_MOD);
		/* TODO even though this is clever, a cleaned-up version of this that didn't rely
		 * on integer casting would be even better because it would remove the need for the NORM_MOD */
		
	}

	/**
	 * @return The frequency of the word in COCA
	 */
	public int getGlobalFrequency() {
		return globalFrequency;
	}

	/**
	 * @return The frequency of the word in the sample with respect to the global frequency (i.e. raw frequency / global frequency)
	 */
	public double getNormalFreq() {
		return normalFreq;
	}

	/**
	 * @return The frequency of the word in the sample 
	 */
	public int getRawFrequency() {
		return rawFrequency;
	}
	
	/**
	 * @return The string (word) represented by this Word object
	 */
	public String getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return value;
	}

	
}
