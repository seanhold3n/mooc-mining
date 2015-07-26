package edu.erau.holdens.moocmining;

/** Enumeration of the phases of learning:<br>
 * <ul>
 * <li> <b>T</b> - Triggering event
 * <li> <b>E</b> - Exploration
 * <li> <b>I</b> - Integration
 * <li> <b>R</b> - Resolution
 * <li> <b>X</b> - Unknown
 * </ul>
 * 
 * @author Sean Holden (holdens@my.erau.edu)
 */
public enum LearningPhase {
	
	T("Triggering event"),
	E("Exploration"),
	I("Integration"),
	R("Resolution"),
	X("Unknown");
	
	private String description;
	
	private LearningPhase(String desc){
		this.description = desc;
	}
	
	/** Returns the description of this learning phase constant 
	 * (for example, <code>LearningPhase.E.toString()</code> will return "Exploration"). 
	 * To get the declared value itself as a string, use {@link #name()}.
	 */
	public String toString(){
		return description;
	}

}
