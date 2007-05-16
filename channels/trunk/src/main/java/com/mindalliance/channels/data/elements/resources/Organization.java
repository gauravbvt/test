/*
 * Created on Apr 26, 2007
 */
package com.mindalliance.channels.data.elements.resources;

import java.util.ArrayList;
import java.util.List;

import com.mindalliance.channels.data.Located;
import com.mindalliance.channels.data.components.Mission;
import com.mindalliance.channels.data.elements.reference.Location;
import com.mindalliance.channels.util.GUID;

/**
 * A resource composed of roles and repositories. An organization may
 * be within a larger organization. An organization also has sharing
 * agreements.
 * 
 * @author jf
 */
public class Organization extends AbstractResource implements Located {

    private List<Mission> missions;
    private Organization parent;
    private Location location;
    private Location jurisdiction;
    private List<Role> roles;
    private List<Repository> repositories;
    private List<Agreement> agreements;
    private List<Group> groups;

    public Organization() {
        super();
    }

    public Organization( GUID guid ) {
        super( guid );
    }

    public Location getLocation() {
        return location;
    }

    /**
     * Get all parent organizations.
     * 
     * @return
     */
    public List<Organization> getParents() {
        List<Organization> parents = new ArrayList<Organization>();
        if ( parent == null )
            return parents;
        else {
            parents.add( parent );
            parents.addAll( parent.getParents() );
            return parents;
        }
    }

    /**
     * @return the agreements
     */
    public List<Agreement> getAgreements() {
        return agreements;
    }

    /**
     * @param agreements the agreements to set
     */
    public void setAgreements( List<Agreement> agreements ) {
        this.agreements = agreements;
    }

    /**
     * @param agreement
     */
    public void addAgreement( Agreement agreement ) {
        agreements.add( agreement );
    }

    /**
     * @param agreement
     */
    public void removeAgreement( Agreement agreement ) {
        agreements.remove( agreement );
    }

    /**
     * @return the groups
     */
    public List<Group> getGroups() {
        return groups;
    }

    /**
     * @param groups the groups to set
     */
    public void setGroups( List<Group> groups ) {
        this.groups = groups;
    }

    /**
     * @param group
     */
    public void addGroup( Group group ) {
        groups.add( group );
    }

    /**
     * @param group
     */
    public void removeGroup( Group group ) {
        groups.remove( group );
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
     * @return the missions
     */
    public List<Mission> getMissions() {
        return missions;
    }

    /**
     * @param missions the missions to set
     */
    public void setMissions( List<Mission> missions ) {
        this.missions = missions;
    }

    /**
     * @param mission
     */
    public void addMission( Mission mission ) {
        missions.add( mission );
    }

    /**
     * @param mission
     */
    public void removeMission( Mission mission ) {
        missions.remove( mission );
    }

    /**
     * @return the parent
     */
    public Organization getParent() {
        return parent;
    }

    /**
     * @param parent the parent to set
     */
    public void setParent( Organization parent ) {
        this.parent = parent;
    }

    /**
     * @return the repositories
     */
    public List<Repository> getRepositories() {
        return repositories;
    }

    /**
     * @param repositories the repositories to set
     */
    public void setRepositories( List<Repository> repositories ) {
        this.repositories = repositories;
    }

    /**
     * @param repository
     */
    public void addRepository( Repository repository ) {
        repositories.add( repository );
    }

    /**
     * @param repository
     */
    public void removeRepository( Repository repository ) {
        repositories.remove( repository );
    }

    /**
     * @return the roles
     */
    public List<Role> getRoles() {
        return roles;
    }

    /**
     * @param roles the roles to set
     */
    public void setRoles( List<Role> roles ) {
        this.roles = roles;
    }

    /**
     * @param role
     */
    public void addRole( Role role ) {
        roles.add( role );
    }

    /**
     * @param role
     */
    public void removeRole( Role role ) {
        roles.remove( role );
    }

    /**
     * @param location the location to set
     */
    public void setLocation( Location location ) {
        this.location = location;
    }
}
