// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.profiles;

import java.util.ArrayList;
import java.util.List;

import com.beanview.annotation.PropertyOptions;
import com.mindalliance.channels.data.definitions.Category.Taxonomy;
import com.mindalliance.channels.data.definitions.Located;
import com.mindalliance.channels.data.definitions.Location;
import com.mindalliance.channels.data.definitions.Organization;
import com.mindalliance.channels.data.support.GUID;
import com.mindalliance.channels.util.CollectionType;

/**
 * A resource composed of roles and repositories. An organization may
 * be within a larger organization. An organization also has sharing
 * agreements.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 *
 * @composed - - * Mission
 * @composed - - * Repository
 * @composed - - * Role
 * @composed - - * Group
 * @navassoc - parent 0..1 Organization
 * @navassoc - liaison 0..1 Person
 */
public class OrganizationImpl extends Resource
    implements Located, Organization {

    private List<Mission> missions = new ArrayList<Mission>();
    private OrganizationImpl parent;
    private Location location;
    private Location jurisdiction;
    private List<Role> roles = new ArrayList<Role>();
    private List<Repository> repositories = new ArrayList<Repository>();
    private List<Group> groups = new ArrayList<Group>();
    private Person liaison;

    /**
     * Default constructor.
     */
    public OrganizationImpl() {
        super();
    }

    /**
     * Default constructor.
     * @param guid the guid
     */
    public OrganizationImpl( GUID guid ) {
        super( guid, Taxonomy.Organization );
    }

    /**
     * Get all parent organizations.
     */
    @PropertyOptions( ignore = true )
    public List<Organization> getParents() {
        List<Organization> parents = new ArrayList<Organization>();
        if ( parent != null ) {
            parents.add( parent );
            parents.addAll( parent.getParents() );
        }
        return parents;
    }

    /**
     * Return the groups.
     */
    @CollectionType( type = Group.class )
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
    @CollectionType( type = Mission.class )
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
    public OrganizationImpl getParent() {
        return parent;
    }

    /**
     * Set the parent organization.
     * @param parent the parent to set
     */
    public void setParent( OrganizationImpl parent ) {
        this.parent = parent;
    }

    /**
     * Return the repositories.
     */
    @CollectionType( type = Repository.class )
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
    @CollectionType( type = Role.class )
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

    /**
     * Return the liaison.
     */
    public Person getLiaison() {
        return this.liaison;
    }

    /**
     * Set the liaison.
     * @param liaison the liaison
     */
    public void setLiaison( Person liaison ) {
        this.liaison = liaison;
    }
}
