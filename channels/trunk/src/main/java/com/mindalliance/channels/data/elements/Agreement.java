// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.elements;

import java.util.List;

import com.mindalliance.channels.data.elements.resources.Organization;
import com.mindalliance.channels.data.reference.Information;
import com.mindalliance.channels.data.reference.Pattern;
import com.mindalliance.channels.util.GUID;

/**
 * An agreement by an organization to carry out specified exchanges
 * with specified organizations.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
public class Agreement extends AbstractElement {

    /** Who makes the commitment. */
    private Organization organization;

    /**
     * Description of information the organization agrees to share.
     */
    private List<Information> informations;

    /** Specification of the recipient organizations. */
    private Pattern<Organization> recipientPattern;

    /**
     * Default constructor.
     */
    public Agreement() {
        super();
    }

    /**
     * Default constructor.
     * @param guid the guid
     */
    public Agreement( GUID guid ) {
        super( guid );
    }

    /**
     * Return the information.
     */
    public List<Information> getInformations() {
        return informations;
    }

    /**
     * Set the informations.
     * @param informations the information to set
     */
    public void setInformations( List<Information> informations ) {
        this.informations = informations;
    }

    /**
     * Add an information.
     * @param information the information
     */
    public void addInformation( Information information ) {
        informations.add( information );
    }

    /**
     * Remove an information.
     * @param information the information
     */
    public void removeInformation( Information information ) {
        informations.remove( information );
    }

    /**
     * Return the organization.
     */
    public Organization getOrganization() {
        return organization;
    }

    /**
     * Set the organization.
     * @param organization the organization to set
     */
    public void setOrganization( Organization organization ) {
        this.organization = organization;
    }

    /**
     * Return the recipient pattern.
     */
    public Pattern<Organization> getRecipientPattern() {
        return recipientPattern;
    }

    /**
     * Set the recipient pattern.
     * @param recipientPattern the recipientPattern to set
     */
    public void setRecipientPattern( Pattern<Organization> recipientPattern ) {
        this.recipientPattern = recipientPattern;
    }
}
