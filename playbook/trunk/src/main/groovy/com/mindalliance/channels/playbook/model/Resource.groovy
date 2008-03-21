package com.mindalliance.channels.playbook.model

/**
 * ...
 */
/*abstract*/ class Resource {

    String name
    String description

    // For display in tables of resources
    String getType() {
        throw new RuntimeException( "Define this..." )
    }

    // Keep for proper maven stub compilation...
    Resource() {
    }

    Resource( String name ) {
        this.name = name
    }

}