package com.mindalliance.channels.playbook.model

/**
 * ...
 */
class Todo implements Serializable {

    String name;
    String priority;
    Date due;

    Todo( String name, String priority, Date due ) {
        this.name = name;
        this.priority = priority;
        this.due = due;
    }
}