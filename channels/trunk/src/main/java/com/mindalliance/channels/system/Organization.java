// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.system;

import java.beans.PropertyVetoException;
import java.util.Collections;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.mindalliance.channels.User;
import com.mindalliance.channels.util.Area;

/**
 * A purposeful administrative structure with defined roles,
 * information resources and possibly sub-organizations. An
 * organization operates within some jurisdiction, perhaps
 * under the authority of other organizations.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 *
 * @opt attributes
 * @navassoc - authorities * Organization
 * @navassoc - parent      1 Organization
 * @navassoc - liaisons * User
 * @composed - - * Role
 * @composed - - * Agent
 */
public class Organization extends SystemObject {

    private String mission;
    private Organization parent;
    private Set<Area> jurisdictions = new TreeSet<Area>();
    private Set<Organization> authorities = new TreeSet<Organization>();
    private Set<Agent> agents;
    private Set<Role> roles;
    private Set<User> liaisons = new TreeSet<User>();

    /**
     * Default constructor.
     */
    public Organization() {
        super();
    }

    /**
     * Default constructor.
     * @param name the name of the organization
     * @throws PropertyVetoException if name clashes with another organization
     */
    public Organization( String name ) throws PropertyVetoException {
        super( name );
    }

    /**
     * Return the value of authorities.
     */
    public Set<Organization> getAuthorities() {
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
     * Return the value of jurisdictions.
     */
    public Set<Area> getJurisdictions() {
        return this.jurisdictions;
    }

    /**
     * Set the value of jurisdictions.
     * @param jurisdictions The new value of jurisdictions
     */
    public void setJurisdictions( Set<Area> jurisdictions ) {
        this.jurisdictions = jurisdictions;
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

    /**
     * Set the value of authorities.
     * @param authorities The new value of authorities
     */
    public void setAuthorities( Set<Organization> authorities ) {
        this.authorities = authorities;
    }

    /**
     * Return the value of liaisons.
     */
    public Set<User> getLiaisons() {
        return Collections.unmodifiableSet( this.liaisons );
    }

    /**
     * Set the value of liaisons.
     * @param liaisons The new value of liaisons
     */
    public void setLiaisons( Set<User> liaisons ) {
        this.liaisons = new TreeSet<User>( liaisons );
    }

    /**
     * Add a liaison.
     * @param liaison a official representant of the organization,
     * able to oversee all things related to this organization.
     */
    public void addLiaision( User liaison ) {
        this.liaisons.add( liaison );
    }

    /**
     * Remove a liaison.
     * @param liaison the liaison
     */
    public void removeLiaison( User liaison ) {
        this.liaisons.remove( liaison );
    }

    /**
     * Return the value of roles.
     */
    public Set<Role> getRoles() {
        return this.roles;
    }

    /**
     * Set the value of roles.
     * @param roles The new value of roles
     */
    public void setRoles( Set<Role> roles ) {
        this.roles = roles;
        // TODO veto on name conflicts
    }

    /**
     * Add a role.
     * @param role a role in this organization
     */
    public void addRole( Role role ) {
        this.roles.add( role );
    }

    /**
     * Remove a role.
     * @param role the role
     */
    public void removeRole( Role role ) {
        this.roles.remove( role );
    }

    /**
     * Return the value of agents.
     */
    public Set<Agent> getAgents() {
        return this.agents;
    }

    /**
     * Set the value of agents.
     * @param agents The new value of agents
     */
    public void setAgents( Set<Agent> agents ) {
        this.agents = agents;
    }

    /**
     * Add an agent.
     * @param agent the agent to add
     */
    public void addAgent( Agent agent ) {
        this.agents.add( agent );
    }

    /**
     * Remove an agent.
     * @param agent the agent
     */
    public void removeAgent( Agent agent ) {
        this.agents.remove( agent );
    }
}
