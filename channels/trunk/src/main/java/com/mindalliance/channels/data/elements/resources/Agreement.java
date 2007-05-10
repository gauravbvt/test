/*
 * Created on Apr 30, 2007
 *
 */
package com.mindalliance.channels.data.elements.resources;

import java.util.List;

import com.mindalliance.channels.data.components.Information;
import com.mindalliance.channels.data.elements.AbstractElement;
import com.mindalliance.channels.data.support.Pattern;
import com.mindalliance.channels.util.GUID;

/**
 * An agreement by an organization to carry out specified exchanges with specified organizations.
 * @author jf
 *
 */
public class Agreement extends AbstractElement {
		
	private Organization organization; // Who makes the commitment
	private List<Information> informations; // Description of information the organization agrees to share
	private Pattern<Organization> recipientPattern; // Specification of the recipient organizations
	
	public Agreement() {
		super();
	}
	public Agreement(GUID guid) {
		super(guid);
	}
	/**
	 * @return the information
	 */
	public List<Information> getInformations() {
		return informations;
	}
	/**
	 * @param information the information to set
	 */
	public void setInformations(List<Information> informations) {
		this.informations = informations;
	}
	/**
	 * 
	 * @param information
	 */
	public void addInformation(Information information) {
		informations.add(information);
	}
	/**
	 * 
	 * @param information
	 */
	public void removeInformation(Information information) {
		informations.remove(information);
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
	 * @return the recipientPattern
	 */
	public Pattern<Organization> getRecipientPattern() {
		return recipientPattern;
	}
	/**
	 * @param recipientPattern the recipientPattern to set
	 */
	public void setRecipientPattern(Pattern<Organization> recipientPattern) {
		this.recipientPattern = recipientPattern;
	}

}
