// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.models;

import java.util.ArrayList;
import java.util.List;

import com.mindalliance.channels.definitions.CategorySet;
import com.mindalliance.channels.support.CollectionType;
import com.mindalliance.channels.support.GUID;

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
public class Task extends Occurrence implements Regulatable {

    private List<Agent> agents = new ArrayList<Agent>();
    private CategorySet objectives;

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
     * Return the agents.
     */
    @CollectionType( type = Agent.class )
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
    public CategorySet getObjectives() {
        return objectives;
    }

    /**
     * Set the objectives.
     * @param objectives the objectives to set
     */
    public void setObjectives( CategorySet objectives ) {
        this.objectives = objectives;
    }

}
