/*
 * Created on Apr 25, 2007
 *
 */
package com.mindalliance.channels.data.elements;

import java.util.List;

import com.mindalliance.channels.User;
import com.mindalliance.channels.data.Element;
import com.mindalliance.channels.data.Named;
import com.mindalliance.channels.data.elements.assertions.Assertion;
import com.mindalliance.channels.data.support.TypeSet;
import com.mindalliance.channels.util.AbstractJavaBean;
import com.mindalliance.channels.util.GUID;

@SuppressWarnings("serial")
/**
 * A generic element
 */
abstract public class AbstractElement extends AbstractJavaBean implements Element {

	private GUID guid;
	private String name;
	private String description;
	private TypeSet typeSet = new TypeSet(this.getClass().getSimpleName());
	private boolean inferred = false;
	private List<Assertion> assertions;
	private List<Issue> issues;
	
	public AbstractElement() {}
	
	public AbstractElement(GUID guid) {
		this.guid = guid;
	}
	
    /**
     * Compares this named object with the specified named object for order.
     * @param o the named object to compare to
     */
    public int compareTo( Named named ) {
        return getName().compareTo( named.getName() );
    }
	
	/* (non-Javadoc)
	 * @see com.mindalliance.channels.data.Element#getDomains()
	 */
	public TypeSet getDomains() {
		return typeSet.getDomains();
	}

	public boolean hasAuthority(User user) {
		return false; // TODO
	}
	
	/* (non-Javadoc)
	 * @see com.mindalliance.channels.data.Element#getIssues()
	 */
	public List<Issue> getIssues() {
		return issues;
	}

	/* (non-Javadoc)
	 * @see com.mindalliance.channels.data.Unique#getGuid()
	 */
	public GUID getGuid() {
		return guid;
	}

	/* (non-Javadoc)
	 * @see com.mindalliance.channels.data.Typed#getTypes()
	 */
	public TypeSet getTypeSet() {
		return typeSet;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the named
	 */
	public String getName() {
		return name;
	}

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
	public void setTypes(TypeSet typeSet) {
		this.typeSet = typeSet;
	}

	/**
	 * @param issues the issues to set
	 */
	public void setIssues(List<Issue> issues) {
		this.issues = issues;
	}
	/**
	 * 
	 * @param issue
	 */
	public void addIssue(Issue issue) {
		issues.add(issue);
	}
	/**
	 * 
	 * @param issue
	 */
	public void removeIssue(Issue issue) {
		issues.remove(issue);
	}

	/**
	 * @return the assertions
	 */
	public List<Assertion> getAssertions() {
		return assertions;
	}

	/**
	 * @param assertions the assertions to set
	 */
	public void setAssertions(List<Assertion> assertions) {
		this.assertions = assertions;
	}
	/**
	 * 
	 * @param assertion
	 */
	public void addAssertion(Assertion assertion) {
		assertions.add(assertion);
	}
	/**
	 * 
	 * @param assertion
	 */
	public void removeAssertion(Assertion assertion) {
		assertions.remove(assertion);
	}

}