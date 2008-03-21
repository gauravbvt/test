package com.mindalliance.channels.playbook.model
/**
 * ...
 */
class Scenario {
    String name
    String description
    Set<Agent> agents = new TreeSet<Agent>()
    List<Occurrence> occurrences = new ArrayList<Occurrence>()

    Scenario( String name ) {
        this.name = name;
    }

    void add( Agent a ) {
        agents.add( a )
    }

    void remove( Agent a ) {
        agents.remove( a )
    }

    void add( Occurrence o ) {
        occurrences.add( o )
    }

    void remove( Occurrence o ) {
        occurrences.remove( o )
    }
}