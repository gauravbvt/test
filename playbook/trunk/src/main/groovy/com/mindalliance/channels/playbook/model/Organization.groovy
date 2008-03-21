package com.mindalliance.channels.playbook.model

/**
 * ...
 */
class Organization extends Resource {

    Organization(String name) {
        super(name);
    }

    String getType() {
        return "Organization";
    }

}