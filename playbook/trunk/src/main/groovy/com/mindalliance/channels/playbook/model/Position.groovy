package com.mindalliance.channels.playbook.model

/**
 * ...
 */
class Position extends Resource {

    Position(String name) {
        super(name);
    }

    String getType() {
        return "Position";
    }
}