package nlp;

/**
 * @author Sean Holden (holdens@my.erau.edu)
 */
public class Word implements Comparable<Word>{

	/** The string (word) represented by this Word object. */
	private String value;
	/** */
	private final int rawFrequency;
	/** */
	private final double globalFrequency;
	/** */
	private final double normalFreq;
	/** Determines the sort order of used in {@link #compareTo(Word)}.  <code>true</code>
	 * will sort the list from low to high; <code>false</code> will sort high to low.  */
	private static final boolean SORT_ASCENDING = false;
	/** A value to arbitrarially (but uniformly) modify the result of 
	 * <code>({@link #rawFrequency} / {@link #globalFrequency})</code> for higher distance 
	 * between data points. */
	private static final int NORM_MOD = 1000;
	
	/** Creates a new word object.
	 * @param value The word itself
	 * @param rawFrequency The frequency of the word in the sample
	 */
	public Word(String value, int rawFrequency, double globalFrequency) {
		super();
		this.value = value;
		this.rawFrequency = rawFrequency;
		this.globalFrequency = globalFrequency;
		this.normalFreq = (rawFrequency / globalFrequency) * NORM_MOD;
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
								:(int)(( o.getNormalFreq() - this.getNormalFreq())*NORM_MOD);
		
	}

	/**
	 * @return the globalFrequency
	 */
	public double getGlobalFrequency() {
		return globalFrequency;
	}

	/** Get the normalized frequency of this word (raw frequency / global frequency)
	 * @return the normalFreq
	 */
	public double getNormalFreq() {
		return normalFreq;
	}

	public int getRawFrequency() {
		return rawFrequency;
	}
	
	public String getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return value;
	}

	
}
