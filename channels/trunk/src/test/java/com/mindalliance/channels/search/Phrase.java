package com.mindalliance.channels.search;

public class Phrase implements Searchable {
	String guid; // globally unique id
	String text; // text to be indexed for search

	public Phrase(String guid, String text) {
	    this.guid = guid;
	    this.text = text;
	}

	public String getGUID() {return guid;}
	
	public String getText() {return text;}

	public String getName() {return "";}
	
	public String getKind() {return "";}
	
	public String getTags() {return "";}
	
	public String getProject() {return "";}
	
	public String getProjectGUID() {return "";}
}
