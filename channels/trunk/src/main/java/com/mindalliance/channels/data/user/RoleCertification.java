/*
 * Created on May 8, 2007
 *
 */
package com.mindalliance.channels.data.user;

import java.util.List;

import com.mindalliance.channels.data.elements.resources.Organization;
import com.mindalliance.channels.data.elements.resources.Person;
import com.mindalliance.channels.util.GUID;

/**
 * Certification that a person does in fact play given roles in an organization.
 * @author jf
 *
 */
public class RoleCertification extends Certification {

	private Person person;
	private Organization organization;
	private List<GUID> roleGUIDs;
	
	public RoleCertification() {
		super();
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
	/**
	 * @return the person
	 */
	public Person getPerson() {
		return person;
	}
	/**
	 * @param person the person to set
	 */
	public void setPerson(Person person) {
		this.person = person;
	}
	/**
	 * @return the roleGUIDs
	 */
	public List<GUID> getRoleGUIDs() {
		return roleGUIDs;
	}
	/**
	 * @param roleGUIDs the roleGUIDs to set
	 */
	public void setRoleGUIDs(List<GUID> roleGUIDs) {
		this.roleGUIDs = roleGUIDs;
	}
	/**
	 * 
	 * @param guid
	 */
	public void addRoleGUID(GUID guid) {
		roleGUIDs.add(guid);
	}
	/**
	 * 
	 * @param guid
	 */
	public void removeRoleGUID(GUID guid) {
		roleGUIDs.remove(guid);
	}
}
