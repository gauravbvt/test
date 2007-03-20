// Copyright (C) 2006 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.mindalliance.channels.JavaBean;
import com.mindalliance.channels.Model;
import com.mindalliance.channels.project.Domain;
import com.mindalliance.channels.system.Agent;
import com.mindalliance.channels.system.Channel;
import com.mindalliance.channels.system.Organization;
import com.mindalliance.channels.system.InformationResource;
import com.mindalliance.channels.util.AbstractJavaBean;

/**
 * The information sharing knowledge that is captured and derived
 * during an ISNA project.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 */
public class ModelImpl extends AbstractJavaBean implements Model {

    private String name;
    private List<String> objectives = new ArrayList<String>();
    private SortedSet<Domain> domains = new TreeSet<Domain>();
    private SortedSet<Mission> missions = new TreeSet<Mission>();
    private SortedSet<Organization> organizations = new TreeSet<Organization>();
    private SortedSet<Channel> channels = new TreeSet<Channel>();
    private SortedSet<Policy> policies = new TreeSet<Policy>();
    private SortedSet<Environment> environments = new TreeSet<Environment>();
    private SortedSet<Agent> agents = new TreeSet<Agent>();
    private SortedSet<InformationResource> resources = new TreeSet<InformationResource>();
    private SortedSet<Agreement> agreements = new TreeSet<Agreement>();

//    private SortedSet<Resolution> resolutions;

    //--------------------------------
    /**
     * Default constructor.
     */
    public ModelImpl() {
        super();
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
    public SortedSet<InformationResource> getResources() {
        return this.resources;
    }

    /**
     * Set the value of resources.
     * @param resources The new value of resources
     */
    public void setResources( SortedSet<InformationResource> resources ) {
        this.resources = resources;
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

    //--------------------------------
    /**
     * Return components that should be asserted by the rule engine.
     * This recursively traverse the components to add their subcomponents.
     */
    public Set<JavaBean> getAssertableObjects() {
        Set<JavaBean> result = new HashSet<JavaBean>();
        result.add( this );
        Collection[] parts = {
                getAgents(),
                getAgreements(),
                getChannels(),
                getDomains(),
                getEnvironments(),
                getMissions(),
                getOrganizations(),
                getPolicies(),
                getResources(),
        };

        for ( Collection<JavaBean> part : parts )
            for ( JavaBean bean : part )
                result.add( bean );

        return result;
    }
}
