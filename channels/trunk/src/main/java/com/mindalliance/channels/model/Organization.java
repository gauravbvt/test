// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.model;

import java.util.SortedSet;
import java.util.TreeSet;

import com.mindalliance.channels.util.GUID;

/**
 * A purposeful administrative structure to which belong agents,
 * information resources and possibly other organizations, and
 * which operates within some jurisdiction and under the authority
 * of other organizations.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 */
public class Organization extends AbstractNamedObject {

    private String kind;
    private String mission;
    private SortedSet<Domain> domains = new TreeSet<Domain>();
    private Organization parent;
    private SortedSet<Area> jurisdictions = new TreeSet<Area>();
    private SortedSet<Organization> authorities = new TreeSet<Organization>();

    /**
     * Default constructor.
     * @param guid the unique ID for this object
     */
    Organization( GUID guid ) {
        super( guid );
    }

    /**
     * Return the value of authorities.
     */
    public SortedSet<Organization> getAuthorities() {
        return this.authorities;
    }

    /**
     * Set the value of authorities.
     * @param authorities The new value of authorities
     */
    public void setAuthorities( SortedSet<Organization> authorities ) {
        this.authorities = authorities;
    }

    /**
     * Return the value of domains.
     */
    public SortedSet<Domain> getDomains() {
        return this.domains;
    }

    /**
     * Set the value of domains.
     * @param domains The new value of domains
     */
    public void setDomains( SortedSet<Domain> domains ) {
        this.domains = domains;
    }

    /**
     * Return the value of jurisdictions.
     */
    public SortedSet<Area> getJurisdictions() {
        return this.jurisdictions;
    }

    /**
     * Set the value of jurisdictions.
     * @param jurisdictions The new value of jurisdictions
     */
    public void setJurisdictions( SortedSet<Area> jurisdictions ) {
        this.jurisdictions = jurisdictions;
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
     * Return the value of mission.
     */
    public String getMission() {
        return this.mission;
    }

    /**
     * Set the value of mission.
     * @param mission The new value of mission
     */
    public void setMission( String mission ) {
        this.mission = mission;
    }

    /**
     * Return the value of parent.
     */
    public Organization getParent() {
        return this.parent;
    }

    /**
     * Set the value of parent.
     * @param parent The new value of parent
     */
    public void setParent( Organization parent ) {
        this.parent = parent;
    }
}
