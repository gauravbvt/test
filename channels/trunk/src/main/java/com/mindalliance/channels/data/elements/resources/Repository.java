/*
 * Created on May 1, 2007
 *
 */
package com.mindalliance.channels.data.elements.resources;

import java.util.List;

import com.mindalliance.channels.data.Accessible;
import com.mindalliance.channels.data.Contactable;
import com.mindalliance.channels.data.components.Information;
import com.mindalliance.channels.util.GUID;
/**
 * A contactable  resource that grants access to information.
 * @author jf
 *
 */
public class Repository extends ContactableResource implements Accessible {
	
	private Organization organization;
	// A specification of what assets the repository can be expected to contain by default.
	private List<Information> contents; 
	private Role administrator; // Role within the administration normally

	

	public Repository() {
		super();
	}

	public Repository(GUID guid) {
		super(guid);
	}

	public boolean hasAccess(Contactable contactable) {
		return false;
	}

	/**
	 * @return the administrator
	 */
	public Role getAdministrator() {
		return administrator;
	}


	/**
	 * @param administrator the administrator to set
	 */
	public void setAdministrator(Role administrator) {
		this.administrator = administrator;
	}


	/**
	 * @return the contents
	 */
	public List<Information> getContents() {
		return contents;
	}


	/**
	 * @param contents the contents to set
	 */
	public void setContents(List<Information> contents) {
		this.contents = contents;
	}
	/**
	 * 
	 * @param information
	 */
	public void addContent(Information information) {
		contents.add(information);
	}
	/**
	 * 
	 * @param information
	 */
	public void removeContent(Information information) {
		contents.remove(information);
	}

	/**
	 * @return the organization
	 */
	public Organization getOrganization() {
		return organization;
	}


	/**
	 * @param organization the organization to set
	 */
	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

}
