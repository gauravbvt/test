package com.mindalliance.channels.playbook.model

/**
 * ...
 */
class Project {
    String name
    String description
    Properties properties = new Properties()

    List scenarios = new ArrayList<Scenario>()
    List resources = new ArrayList<Resource>()

    void addScenario( Scenario s ) {
        scenarios.add( s )
    }

    void addResource( Resource s ) {
        resources.add( s )
    }
}