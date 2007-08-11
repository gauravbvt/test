// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.profiles;

import java.util.ArrayList;
import java.util.List;

import com.beanview.annotation.PropertyOptions;
import com.mindalliance.channels.definitions.Location;
import com.mindalliance.channels.definitions.Category.Taxonomy;
import com.mindalliance.channels.support.CollectionType;
import com.mindalliance.channels.support.GUID;

/**
 * A role in an organization either played by identified persons or by
 * a group of anonymous, interchangeable persons. A role can be
 * internal to the organization (not accessible from the outside) or
 * external.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
public class Role extends Actor {

    private OrganizationImpl organization;
    private boolean isInternal;
    private List<Role> managers;
    private List<Role> alternates;
    private boolean pointOfContact;
    private Location jurisdiction;

    /**
     * Default constructor.
     */
    public Role() {
        this( null, Taxonomy.Role );
    }

    /**
     * Default constructor.
     * @param guid the guid
     * @param taxonomy the taxonomy
     */
    public Role( GUID guid, Taxonomy taxonomy ) {
        super( guid, taxonomy );
    }

    /**
     * Overriden from Actor.
     * @see com.mindalliance.channels.profiles.Actor#getRoles()
     */
    @PropertyOptions( ignore = true )
    public List<Role> getRoles() {
        List<Role> roles = new ArrayList<Role>();
        roles.add( this );
        return roles;
    }

    /**
     * Return the alternates for this role.
     */
    @CollectionType( type = Role.class )
    public List<Role> getAlternates() {
        return alternates;
    }

    /**
     * Set the alternates for this role.
     * @param alternates the alternates to set
     */
    public void setAlternates( List<Role> alternates ) {
        this.alternates = alternates;
    }

    /**
     * Add an alternate.
     * @param role the alternate role
     */
    public void addAlternate( Role role ) {
        alternates.add( role );
    }

    /**
     * Remove an alternate.
     * @param role the alternate role
     */
    public void removeAlternate( Role role ) {
        alternates.remove( role );
    }

    /**
     * Return true if this role is known only internally
     * to the organization (e.g. morale officer).
     */
    public boolean isInternal() {
        return isInternal;
    }

    /**
     * Specify if the role is internal to the organization.
     * @param isInternal the internal status
     */
    public void setInternal( boolean isInternal ) {
        this.isInternal = isInternal;
    }

    /**
     * Return the jurisdiction.
     */
    public Location getJurisdiction() {
        return jurisdiction;
    }

    /**
     * Set the jurisdiction.
     * @param jurisdiction the jurisdiction to set
     */
    public void setJurisdiction( Location jurisdiction ) {
        this.jurisdiction = jurisdiction;
    }

    /**
     * Return the managers.
     */
    @CollectionType( type = Role.class )
    public List<Role> getManagers() {
        return managers;
    }

    /**
     * Set the managers.
     * @param managers the managers
     */
    public void setManagers( List<Role> managers ) {
        this.managers = managers;
    }

    /**
     * Add a manager.
     * @param role the manager role
     */
    public void addManager( Role role ) {
        managers.add( role );
    }

    /**
     * Remove a manager.
     * @param role the manager role
     */
    public void removeManager( Role role ) {
        managers.remove( role );
    }

    /**
     * Return the organization.
     */
    public OrganizationImpl getOrganization() {
        return organization;
    }

    /**
     * Set the organization.
     * @param organization the organization
     */
    public void setOrganization( OrganizationImpl organization ) {
        this.organization = organization;
    }

    /**
     * Return if this role is a point of contact for the organization.
     */
    public boolean isPointOfContact() {
        return pointOfContact;
    }

    /**
     * Return if this role is a point of contact for the organization.
     * @param pointOfContact the pointOfContact information
     */
    public void setPointOfContact( boolean pointOfContact ) {
        this.pointOfContact = pointOfContact;
    }

}
