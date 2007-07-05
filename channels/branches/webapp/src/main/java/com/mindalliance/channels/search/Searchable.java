package com.mindalliance.channels.search;

/**
 * Objects that meet this interface can be indexed for text-based search.
 * 
 * @author ebax
 *
 */

public interface Searchable {
	/**
	 * @return globally unique id
	 */
	public String getGUID();
	
	/**
	 * @return text to be indexed for search, which may include text of child objects
	 */
	public String getText();
	
	/**
	 * @return name of object, for display to user in search results
	 */
	public String getName();
	
	/**
	 * @return object class, e.g. event, task, for filtering search results
	 */
	public String getKind();
	
	/**
	 * @return object tags
	 */
	public String getTags();
	
	/**
	 * @return display name of project
	 */
	public String getProject();
	
	/**
	 * @return globally unique id of project
	 */
	public String getProjectGUID();
}
