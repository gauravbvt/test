// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.mindalliance.channels.JavaBean;
import com.mindalliance.channels.Model;
import com.mindalliance.channels.Project;
import com.mindalliance.channels.project.AbstractProjectObject;
import com.mindalliance.channels.reference.Policy;
import com.mindalliance.channels.system.Channel;
import com.mindalliance.channels.system.Organization;
import com.mindalliance.channels.system.Role;

/**
 * A collection of scenarios that together fulfill a number of
 * communications planning objectives.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 *
 * @opt attributes
 * @navassoc - - * Policy
 * @navassoc - - * Role
 * @navassoc - - * Organization
 * @navassoc - - * Channel
 * @composed - - * Environment
 * @composed - - * Scenario
 */
public class ModelImpl extends AbstractProjectObject
        implements Model {

    private String name;
    private List<String> objectives = new ArrayList<String>();
    private Set<Policy> policies = new TreeSet<Policy>();
    private Set<Environment> environments = new TreeSet<Environment>();
    private Set<Scenario> scenarios = new TreeSet<Scenario>();
    private Set<Organization> organizations = new TreeSet<Organization>();
    private Set<Role> roles = new TreeSet<Role>();
    private Set<Channel> channels = new TreeSet<Channel>();

    /**
     * Default constructor.
     */
    public ModelImpl() {
        super();
    }

    /**
     * Default constructor.
     * @param project the project
     */
    public ModelImpl( Project project ) {
        super( project );
    }

    /**
     * Return the value of channels.
     */
    public Set<Channel> getChannels() {
        return this.channels;
    }

    /**
     * Set the value of channels.
     * @param channels The new value of channels
     */
    public void setChannels( Set<Channel> channels ) {
        this.channels = channels;
    }

    /**
     * Return the value of environments.
     */
    public Set<Environment> getEnvironments() {
        return this.environments;
    }

    /**
     * Set the value of environments.
     * @param environments The new value of environments
     */
    public void setEnvironments( Set<Environment> environments ) {
        this.environments = environments;
    }

    /**
     * Return the value of name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Set the value of name.
     * @param name The new value of name
     */
    public void setName( String name ) {
        this.name = name;
    }

    /**
     * Return the value of objectives.
     */
    public List<String> getObjectives() {
        return this.objectives;
    }

    /**
     * Set the value of objectives.
     * @param objectives The new value of objectives
     */
    public void setObjectives( List<String> objectives ) {
        this.objectives = objectives;
    }

    /**
     * Return the value of organizations.
     */
    public Set<Organization> getOrganizations() {
        return this.organizations;
    }

    /**
     * Set the value of organizations.
     * @param organizations The new value of organizations
     */
    public void setOrganizations( Set<Organization> organizations ) {
        this.organizations = organizations;
    }

    /**
     * Return the value of policies.
     */
    public Set<Policy> getPolicies() {
        return this.policies;
    }

    /**
     * Set the value of policies.
     * @param policies The new value of policies
     */
    public void setPolicies( Set<Policy> policies ) {
        this.policies = policies;
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
    }

    /**
     * Return the value of scenarios.
     */
    public Set<Scenario> getScenarios() {
        return this.scenarios;
    }

    /**
     * Set the value of scenarios.
     * @param scenarios The new value of scenarios
     */
    public void setScenarios( Set<Scenario> scenarios ) {
        this.scenarios = new TreeSet<Scenario>( scenarios );
        for ( Scenario scenario : scenarios )
            scenario.setModel( this );
    }

    /**
     * Add a scenario to this model.
     * @param scenario the new scenario
     */
    public void addScenario( Scenario scenario ) {
        this.scenarios.add( scenario );
        scenario.setModel( this );
    }

    /**
     * Remove a scenario.
     * @param scenario the scenario
     */
    public void removeScenario( Scenario scenario ) {
        this.scenarios.remove( scenario );
        scenario.setModel( null );
    }

    /**
     * Return components that should be asserted by the rule engine.
     */
    public Set<JavaBean> getAssertableObjects() {
        return null;
    }

    /**
     * Compares this object with the specified object for order.
     * @param o the other model
     */
    public int compareTo( Model o ) {
        return getName().compareTo( o.getName() );
    }

    /**
     * Provide a printed representation.
     */
    @Override
    public String toString() {
        return getName();
    }
}
