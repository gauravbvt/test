/*
 * Created on Apr 25, 2007
 *
 */
package com.mindalliance.channels.data.elements;

import java.util.List;

import com.mindalliance.channels.User;
import com.mindalliance.channels.data.Element;
import com.mindalliance.channels.data.TypeSet;
import com.mindalliance.channels.data.beans.Issue;
import com.mindalliance.channels.util.AbstractJavaBean;
import com.mindalliance.channels.util.GUID;

@SuppressWarnings("serial")
abstract public class AbstractElement extends AbstractJavaBean implements Element {

	private GUID guid;
	private String name;
	private String description;
	private TypeSet types = new TypeSet(this.getClass().getSimpleName());
	private boolean inferred = false;
	private List<Issue> issues;
	
	/**
	 * @return the inferred
	 */
	public boolean isInferred() {
		return inferred;
	}

	/**
	 * @param inferred the inferred to set
	 */
	public void setInferred(boolean inferred) {
		this.inferred = inferred;
	}

	public AbstractElement() {}
	
	public AbstractElement(GUID guid) {
		this.guid = guid;
	}
	
	public boolean hasAuthority(User user) {
		return false;
	}
	
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @return the guid
	 */
	public GUID getGuid() {
		return guid;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @return the types
	 */
	public TypeSet getTypes() {
		return types;
	}

	/**
	 * @return the issues
	 */
	public List<Issue> getIssues() {
		return issues;
	}

	/**
	 * @param issues the issues to set
	 */
	public void setIssues(List<Issue> issues) {
		this.issues = issues;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @param guid the guid to set
	 */
	public void setGuid(GUID guid) {
		this.guid = guid;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param types the types to set
	 */
	public void setTypes(TypeSet types) {
		this.types = types;
	}

}