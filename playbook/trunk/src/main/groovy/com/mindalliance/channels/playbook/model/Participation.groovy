package com.mindalliance.channels.playbook.model

/**
 * ...
 */
class Participation {
    User user
    Project project
    boolean analyst
    Person person
    List<Todo> todos = new ArrayList<Todo>()

    Participation( User user, Project project ) {
        if ( user == null || project == null )
            throw new NullPointerException()
        this.user = user
        this.project = project
    }
}