// Copyright (C) 2006 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.model;

import java.util.SortedSet;
import java.util.TreeSet;

import com.mindalliance.channels.Model;
import com.mindalliance.channels.impl.AbstractJavaBean;

import ognl.Ognl;
import ognl.OgnlException;

/**
 * The information sharing knowledge that is captured and derived
 * during an ISNA project.
 *
 * @see <a href="http://www.ognl.org/">OGNL website</a>
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 */
public class ModelImpl extends AbstractJavaBean implements Model {

    private String name;
    private SortedSet<Domain> domains = new TreeSet<Domain>();
    private SortedSet<Mission> missions = new TreeSet<Mission>();
    private SortedSet<Organization> organizations = new TreeSet<Organization>();
    private SortedSet<Channel> channels = new TreeSet<Channel>();
    private SortedSet<Policy> policies = new TreeSet<Policy>();
    private SortedSet<Environment> environments = new TreeSet<Environment>();
    private SortedSet<Agent> agents = new TreeSet<Agent>();
    private SortedSet<Resource> resources = new TreeSet<Resource>();
    private SortedSet<Agreement> agreements = new TreeSet<Agreement>();

//    private SortedSet<Resolution> resolutions;

    //--------------------------------
    /**
     * Default constructor.
     */
    public ModelImpl() {
        super();
    }

    //--------------------------------
    /**
     * Get an object using an OGNL path.
     * @param path the path
     * @return the object
     * @throws ModelException on errors
     */
    public Object get( Object path ) throws ModelException {

        try {
            return Ognl.getValue( path, this );

        } catch ( OgnlException ex ) {
            throw new ModelException( ex );
        }
    }

    /**
     * Set an object using an OGNL path.
     * @param path the path
     * @param value the new value
     * @throws ModelException on errors
     */
    public void set( Object path, Object value ) throws ModelException {

        try {
            Ognl.setValue( path, this, value );

        } catch ( OgnlException ex ) {
            throw new ModelException( ex );
        }
    }

    /**
     * Return the value of agents.
     */
    public SortedSet<Agent> getAgents() {
        return this.agents;
    }

    /**
     * Set the value of agents.
     * @param agents The new value of agents
     */
    public void setAgents( SortedSet<Agent> agents ) {
        this.agents = agents;
    }

    /**
     * Return the value of agreements.
     */
    public SortedSet<Agreement> getAgreements() {
        return this.agreements;
    }

    /**
     * Set the value of agreements.
     * @param agreements The new value of agreements
     */
    public void setAgreements( SortedSet<Agreement> agreements ) {
        this.agreements = agreements;
    }

    /**
     * Return the value of channels.
     */
    public SortedSet<Channel> getChannels() {
        return this.channels;
    }

    /**
     * Set the value of channels.
     * @param channels The new value of channels
     */
    public void setChannels( SortedSet<Channel> channels ) {
        this.channels = channels;
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
     * Return the value of environments.
     */
    public SortedSet<Environment> getEnvironments() {
        return this.environments;
    }

    /**
     * Set the value of environments.
     * @param environments The new value of environments
     */
    public void setEnvironments( SortedSet<Environment> environments ) {
        this.environments = environments;
    }

    /**
     * Return the value of missions.
     */
    public SortedSet<Mission> getMissions() {
        return this.missions;
    }

    /**
     * Set the value of missions.
     * @param missions The new value of missions
     */
    public void setMissions( SortedSet<Mission> missions ) {
        this.missions = missions;
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
     * Return the value of organizations.
     */
    public SortedSet<Organization> getOrganizations() {
        return this.organizations;
    }

    /**
     * Set the value of organizations.
     * @param organizations The new value of organizations
     */
    public void setOrganizations( SortedSet<Organization> organizations ) {
        this.organizations = organizations;
    }

    /**
     * Return the value of policies.
     */
    public SortedSet<Policy> getPolicies() {
        return this.policies;
    }

    /**
     * Set the value of policies.
     * @param policies The new value of policies
     */
    public void setPolicies( SortedSet<Policy> policies ) {
        this.policies = policies;
    }

    /**
     * Return the value of resources.
     */
    public SortedSet<Resource> getResources() {
        return this.resources;
    }

    /**
     * Set the value of resources.
     * @param resources The new value of resources
     */
    public void setResources( SortedSet<Resource> resources ) {
        this.resources = resources;
    }
}
