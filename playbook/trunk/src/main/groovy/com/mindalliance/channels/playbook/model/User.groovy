package com.mindalliance.channels.playbook.model

/**
 * ...
 */
class User {
    String id
    String name
    String password
    boolean admin

    User( String id, String name, String password ) {
        this.id = id
        this.name = name
        this.password = password
    }

    String toString() {
        return name
    }
}