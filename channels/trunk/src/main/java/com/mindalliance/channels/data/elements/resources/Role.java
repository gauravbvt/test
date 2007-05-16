/*
 * Created on Apr 26, 2007
 */
package com.mindalliance.channels.data.elements.resources;

import java.util.ArrayList;
import java.util.List;

import com.mindalliance.channels.data.Actor;
import com.mindalliance.channels.data.elements.project.Project;
import com.mindalliance.channels.data.reference.Location;
import com.mindalliance.channels.util.GUID;

/**
 * A role in an organization either played by identified persons or by
 * a group of anonymous, interchangeable persons. A role can be
 * internal to the organization (not accessible from the outside) or
 * external.
 * 
 * @author jf
 */
public class Role extends AccessibleResource implements Actor {

    private Organization organization;
    // Either persons or group is set, not both.
    private boolean isInternal; // Is this role known only internally
                                // to the organization (e.g. morale
                                // officer)
    private List<Role> managers;
    private List<Role> alternates;
    private boolean pointOfContact; // is a point of contact for the
                                    // organization
    private Location jurisdiction; // Area of jurisdiction for the
                                    // role

    public List<Role> getRoles( Project project ) { // TODO Check if
                                                    // role still in
                                                    // scope of
                                                    // project
        List<Role> roles = new ArrayList();
        roles.add( this );
        return roles;
    }

    public Role() {
        super();
    }

    public Role( GUID guid ) {
        super( guid );
    }

    /**
     * @return the alternates
     */
    public List<Role> getAlternates() {
        return alternates;
    }

    /**
     * @param alternates the alternates to set
     */
    public void setAlternates( List<Role> alternates ) {
        this.alternates = alternates;
    }

    /**
     * @param role
     */
    public void addAlternate( Role role ) {
        alternates.add( role );
    }

    /**
     * @param role
     */
    public void removeAlternate( Role role ) {
        alternates.remove( role );
    }

    /**
     * @return the isInternal
     */
    public boolean isInternal() {
        return isInternal;
    }

    /**
     * @param isInternal the isInternal to set
     */
    public void setInternal( boolean isInternal ) {
        this.isInternal = isInternal;
    }

    /**
     * @return the jurisdiction
     */
    public Location getJurisdiction() {
        return jurisdiction;
    }

    /**
     * @param jurisdiction the jurisdiction to set
     */
    public void setJurisdiction( Location jurisdiction ) {
        this.jurisdiction = jurisdiction;
    }

    /**
     * @return the managers
     */
    public List<Role> getManagers() {
        return managers;
    }

    /**
     * @param managers the managers to set
     */
    public void setManagers( List<Role> managers ) {
        this.managers = managers;
    }

    /**
     * @param role
     */
    public void addManager( Role role ) {
        managers.add( role );
    }

    /**
     * @param role
     */
    public void removeManager( Role role ) {
        managers.remove( role );
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
    public void setOrganization( Organization organization ) {
        this.organization = organization;
    }

    /**
     * @return the pointOfContact
     */
    public boolean isPointOfContact() {
        return pointOfContact;
    }

    /**
     * @param pointOfContact the pointOfContact to set
     */
    public void setPointOfContact( boolean pointOfContact ) {
        this.pointOfContact = pointOfContact;
    }

}
