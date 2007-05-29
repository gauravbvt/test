// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.elements.resources;

import java.util.ArrayList;
import java.util.List;

import com.beanview.annotation.PropertyOptions;
import com.mindalliance.channels.data.components.Mission;
import com.mindalliance.channels.data.elements.Agreement;
import com.mindalliance.channels.data.reference.Located;
import com.mindalliance.channels.data.reference.Location;
import com.mindalliance.channels.util.CollectionType;
import com.mindalliance.channels.util.GUID;

/**
 * A resource composed of roles and repositories. An organization may
 * be within a larger organization. An organization also has sharing
 * agreements.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
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

    /**
     * Default constructor.
     */
    public Organization() {
        super();
    }

    /**
     * Default constructor.
     * @param guid the guid
     */
    public Organization( GUID guid ) {
        super( guid );
    }

    /**
     * Get all parent organizations.
     */
    @PropertyOptions(ignore=true)
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
     * Return the agreements.
     */
    @CollectionType(type=Agreement.class)
    public List<Agreement> getAgreements() {
        return agreements;
    }

    /**
     * Set the agreements.
     * @param agreements the agreements to set
     */
    public void setAgreements( List<Agreement> agreements ) {
        this.agreements = agreements;
    }

    /**
     * Add an agreement.
     * @param agreement the agreement
     */
    public void addAgreement( Agreement agreement ) {
        agreements.add( agreement );
    }

    /**
     * Remove an agreement.
     * @param agreement the agreement
     */
    public void removeAgreement( Agreement agreement ) {
        agreements.remove( agreement );
    }

    /**
     * Return the groups.
     */
    @CollectionType(type=Group.class)
    public List<Group> getGroups() {
        return groups;
    }

    /**
     * Set the groups.
     * @param groups the groups to set
     */
    public void setGroups( List<Group> groups ) {
        this.groups = groups;
    }

    /**
     * Add a group.
     * @param group the group
     */
    public void addGroup( Group group ) {
        groups.add( group );
    }

    /**
     * Remove a group.
     * @param group the group
     */
    public void removeGroup( Group group ) {
        groups.remove( group );
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
     * Return the missions.
     */
    @CollectionType(type=Mission.class)
    public List<Mission> getMissions() {
        return missions;
    }

    /**
     * Set the missions.
     * @param missions the missions to set
     */
    public void setMissions( List<Mission> missions ) {
        this.missions = missions;
    }

    /**
     * Add a mission.
     * @param mission the mission
     */
    public void addMission( Mission mission ) {
        missions.add( mission );
    }

    /**
     * Remove a mission.
     * @param mission the mission
     */
    public void removeMission( Mission mission ) {
        missions.remove( mission );
    }

    /**
     * Return the parent organization.
     */
    public Organization getParent() {
        return parent;
    }

    /**
     * Set the parent organization.
     * @param parent the parent to set
     */
    public void setParent( Organization parent ) {
        this.parent = parent;
    }

    /**
     * Return the repositories.
     */
    @CollectionType(type=Repository.class)
    public List<Repository> getRepositories() {
        return repositories;
    }

    /**
     * Set the repositories.
     * @param repositories the repositories to set
     */
    public void setRepositories( List<Repository> repositories ) {
        this.repositories = repositories;
    }

    /**
     * Add a repository.
     * @param repository the repository
     */
    public void addRepository( Repository repository ) {
        repositories.add( repository );
    }

    /**
     * Remove a repository.
     * @param repository the repository
     */
    public void removeRepository( Repository repository ) {
        repositories.remove( repository );
    }

    /**
     * Return the roles.
     */
    @CollectionType(type=Role.class)
    public List<Role> getRoles() {
        return roles;
    }

    /**
     * Set the roles.
     * @param roles the roles to set
     */
    public void setRoles( List<Role> roles ) {
        this.roles = roles;
    }

    /**
     * Add a role.
     * @param role the role
     */
    public void addRole( Role role ) {
        roles.add( role );
    }

    /**
     * Remove a role.
     * @param role the role
     */
    public void removeRole( Role role ) {
        roles.remove( role );
    }

    /**
     * Set the location.
     * @param location the location to set
     */
    public void setLocation( Location location ) {
        this.location = location;
    }

    /**
     * Get the location.
     */
    public Location getLocation() {
        return location;
    }
}
