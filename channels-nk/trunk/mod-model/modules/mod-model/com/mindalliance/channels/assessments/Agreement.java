// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.assessments;

import java.util.List;

import com.mindalliance.channels.definitions.Information;
import com.mindalliance.channels.definitions.Organization;
import com.mindalliance.channels.profiles.InferableObject;
import com.mindalliance.channels.support.GUID;

/**
 * An agreement by an organization to carry out specified exchanges
 * with specified organizations.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
public class Agreement extends InferableObject {

    /** Who makes the commitment. */
    private Organization organization;

    /**
     * Description of information the organization agrees to share.
     */
    private List<Information> informations;

    /** Specification of the recipient organizations. */
    private List<Organization> recipients;

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
     * @param organization the organization to
     */
    public void setOrganization( Organization organization ) {
        this.organization = organization;
    }

    /**
     * Return the recipients.
     */
    public List<Organization> getRecipients() {
        return recipients;
    }

    /**
     * Set the recipients.
     * @param recipients the recipients
     */
    public void setRecipients( List<Organization> recipients ) {
        this.recipients = recipients;
    }

    /**
     * Add a recipient.
     * @param recipient the recipient
     */
    public void addRecipient( Organization recipient ) {
        this.recipients.add( recipient );
    }

    /**
     * Remove a recipient.
     * @param recipient the recipient
     */
    public void removeRecipient( Organization recipient ) {
        this.recipients.remove( recipient );
    }
}
