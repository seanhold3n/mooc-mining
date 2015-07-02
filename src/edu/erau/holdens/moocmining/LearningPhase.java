package edu.erau.holdens.moocmining;

public enum LearningPhase {
	
	T("Triggering event"),
	E("Exploration"),
	I("Integration"),
	R("Resolution");
	
	private String description;
	
	private LearningPhase(String desc){
		this.description = desc;
	}
	
	public String toString(){
		return description;
	}

}
