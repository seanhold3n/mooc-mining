package edu.erau.holdens.moocmining;


public class DiscussionEntry {
	
	/** The number of the entry in the discussion */
	private int number;
	/** The text of the discussion */
	private String text;
	/** The phase of learning that describes this discussion item */
	private LearningPhase phase;
	
	/** Creates a new {@link DiscussionEntry} object.
	 * @param number The number of the entry in the discussion 
	 * @param text The text of the discussion
	 * @param phase The phase of learning that describes this discussion item
	 */
	public DiscussionEntry(int number, String text, LearningPhase phase) {
		super();
		this.number = number;
		this.text = text;
		this.phase = phase;
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
	

}
