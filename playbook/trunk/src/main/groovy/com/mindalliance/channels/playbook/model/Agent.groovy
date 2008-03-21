package com.mindalliance.channels.playbook.model

/**
 * ...
 */
class Agent implements Comparable {
    String id
    String name
    String description

    // Keep for proper maven stub compilation...
    Agent() {
    }

    Agent( String name ) {
        if ( name == null )
            throw new NullPointerException()
        this.name = name
    }
    
    int compareTo(Object o) {
        if ( o != null && o instanceof Agent )
            return name.compareTo( ((Agent) o).getName() )
        else
            throw new IllegalArgumentException()
    }
}