package com.mindalliance.channels.playbook.model

/**
 * ...
 */
class AgentGroup extends Agent {

    Set<Agent> agents = new TreeSet<Agent>()

    AgentGroup( String name ) {
        super( name )
    }

    void add( Agent a ) {
        agents.add( a )
    }

    void remove( Agent a ) {
        agents.remove( a )
    }

}