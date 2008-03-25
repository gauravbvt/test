package com.mindalliance.channels.playbook.ifm

import com.mindalliance.channels.playbook.ref.Ref

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Mar 21, 2008
* Time: 11:42:26 AM
*/
class Participation extends IfmElement {

    Ref user
    Ref project
    Boolean analyst
    Ref person
    List<Ref> todos = []

    void addTodo( Todo todo ) {
        todos.add( todo )
        changed( "todos" )
    }

    void removeTodo( Todo todo ) {
        todos.remove( todo )
        changed( "todos" )
    }

}