package edu.erau.holdens.moocmining;


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
	
	public String toString(){
		return String.format("Number: %d; Author: %s; Phase: %s; Text: %s", number, author, phase.name(), text);
	}

}
