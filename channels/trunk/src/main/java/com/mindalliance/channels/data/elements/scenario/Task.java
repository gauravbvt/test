// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.elements.scenario;

import java.util.ArrayList;
import java.util.List;

import com.beanview.annotation.PropertyOptions;
import com.mindalliance.channels.data.Regulatable;
import com.mindalliance.channels.data.components.Caused;
import com.mindalliance.channels.data.elements.assertions.Assertion;
import com.mindalliance.channels.data.elements.assertions.Regulated;
import com.mindalliance.channels.data.reference.TypeSet;
import com.mindalliance.channels.util.CollectionType;
import com.mindalliance.channels.util.GUID;

/**
 * A specification of activities. It is carried out by agents which
 * specify roles or teams. A task is caused either by an event or a
 * task (sub-task). It can create knowledge, produce artefacts and
 * cause events and tasks. A Task represents a separate activity for
 * each role or team that matches one of its agents.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
public class Task extends AbstractOccurrence implements Caused, Regulatable {

    private List<Agent> agents = new ArrayList<Agent>();
    private TypeSet objectives;

    /**
     * Default constructor.
     */
    public Task() {
        super();
    }

    /**
     * Default constructor.
     * @param guid the guid
     */
    public Task( GUID guid ) {
        super( guid );
    }

    /**
     * Get a list of regulated assertions.
     */
    @PropertyOptions(ignore=true)
    public List<Regulated> getRegulatedAssertions() {
        List<Regulated> regulatedAssertions = new ArrayList<Regulated>();
        for ( Assertion assertion : getAssertions() ) {
            if ( assertion instanceof Regulated )
                regulatedAssertions.add( (Regulated) assertion );
        }
        return regulatedAssertions;
    }

    /**
     * Return the agents.
     */
    @CollectionType(type=Agent.class)
    public List<Agent> getAgents() {
        return agents;
    }

    /**
     * Set the agents.
     * @param agents the agents to set
     */
    public void setAgents( List<Agent> agents ) {
        this.agents = agents;
    }

    /**
     * Add an agent.
     * @param agent the agent
     */
    public void addAgent( Agent agent ) {
        agents.add( agent );
    }

    /**
     * Remove an agent.
     * @param agent the agent
     */
    public void removeAgent( Agent agent ) {
        agents.remove( agent );
    }

    /**
     * Return the objectives.
     */
    public TypeSet getObjectives() {
        return objectives;
    }

    /**
     * Set the objectives.
     * @param objectives the objectives to set
     */
    public void setObjectives( TypeSet objectives ) {
        this.objectives = objectives;
    }

}
