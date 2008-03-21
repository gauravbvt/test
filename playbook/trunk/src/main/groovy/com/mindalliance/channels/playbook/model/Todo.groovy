package com.mindalliance.channels.playbook.model

/**
 * ...
 */
class Todo implements Serializable {

    String description = "";
    String priority = "Normal" ;
    Date due = new Date( System.currentTimeMillis() );

    Todo() {
    }

    Todo( String description, String priority, Date due ) {
        this()
        this.description = description;
        this.priority = priority;
        this.due = due;
    }
}