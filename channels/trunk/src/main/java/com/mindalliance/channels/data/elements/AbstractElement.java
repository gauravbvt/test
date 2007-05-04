/*
 * Created on Apr 25, 2007
 *
 */
package com.mindalliance.channels.data.elements;

import java.util.List;

import com.mindalliance.channels.User;
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
	private TypeSet types = new TypeSet(this.getClass().getSimpleName());
	private boolean inferred = false;
	public AbstractElement() {}
	
	public AbstractElement(GUID guid) {
		this.guid = guid;
	}
	
	public boolean hasAuthority(User user) {
		return false;
	}
	
	/* (non-Javadoc)
	 * @see com.mindalliance.channels.data.Element#getIssues()
	 */
	public List<Issue> getIssues() {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.mindalliance.channels.data.Unique#getGuid()
	 */
	public GUID getGuid() {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.mindalliance.channels.data.Typed#getTypes()
	 */
	public TypeSet getTypes() {
		return null;
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


}