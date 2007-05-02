package com.mindalliance.channels.search;

/**
 * A Phrase is a Searchable object used only for testing.
 * @author ebax
 *
 */

public class Phrase implements Searchable {
	String guid; // globally unique id
	String text; // text to be indexed for search

	/**
	 * Simple constructor to test only GUID and text search functionality.
	 * 
	 * @param guid
	 * @param text
	 */
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
