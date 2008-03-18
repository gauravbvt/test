package com.mindalliance.channels.playbook.model

/**
 * ...
 */
class Project {
    String name
    String description
    List<Scenario> scenarios = new ArrayList<Scenario>()
    List<Resource> resources = new ArrayList<Resource>()

    void add( Scenario s ) {
        scenarios.add( s )
    }
    
    void add( Resource s ) {
        resources.add( s )
    }
}