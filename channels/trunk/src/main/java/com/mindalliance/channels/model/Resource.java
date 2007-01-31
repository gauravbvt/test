// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.model;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import com.mindalliance.channels.GUID;

/**
 * A non-human resource of some kind, such as a database, Web portal
 * or library, that is administered by an organization, and holds
 * and accepts information it then would make accessible.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 */
public class Resource extends AbstractNamedObject {

    private String kind;
    private Organization organization;
    private SortedSet<Agent> administrators = new TreeSet<Agent>();
    private ContactInfo contactInfo;
    private List<InformationAsset> knows = new ArrayList<InformationAsset>();
    private List<InformationNeed> needsToKnow =
                    new ArrayList<InformationNeed>();

    /**
     * Default constructor.
     * @param guid the unique ID for this object
     */
    Resource( GUID guid ) {
        super( guid );
    }

    /**
     * Return the value of administrators.
     */
    public SortedSet<Agent> getAdministrators() {
        return this.administrators;
    }

    /**
     * Set the value of administrators.
     * @param administrators The new value of administrators
     */
    public void setAdministrators( SortedSet<Agent> administrators ) {
        this.administrators = administrators;
    }

    /**
     * Return the value of contactInfo.
     */
    public ContactInfo getContactInfo() {
        return this.contactInfo;
    }

    /**
     * Set the value of contactInfo.
     * @param contactInfo The new value of contactInfo
     */
    public void setContactInfo( ContactInfo contactInfo ) {
        this.contactInfo = contactInfo;
    }

    /**
     * Return the value of kind.
     */
    public String getKind() {
        return this.kind;
    }

    /**
     * Set the value of kind.
     * @param kind The new value of kind
     */
    public void setKind( String kind ) {
        this.kind = kind;
    }

    /**
     * Return the value of knows.
     */
    public List<InformationAsset> getKnows() {
        return this.knows;
    }

    /**
     * Set the value of knows.
     * @param knows The new value of knows
     */
    public void setKnows( List<InformationAsset> knows ) {
        this.knows = knows;
    }

    /**
     * Return the value of needsToKnow.
     */
    public List<InformationNeed> getNeedsToKnow() {
        return this.needsToKnow;
    }

    /**
     * Set the value of needsToKnow.
     * @param needsToKnow The new value of needsToKnow
     */
    public void setNeedsToKnow( List<InformationNeed> needsToKnow ) {
        this.needsToKnow = needsToKnow;
    }

    /**
     * Return the value of organization.
     */
    public Organization getOrganization() {
        return this.organization;
    }

    /**
     * Set the value of organization.
     * @param organization The new value of organization
     */
    public void setOrganization( Organization organization ) {
        this.organization = organization;
    }
}
