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

    // TODO
    // make list computable from Todos as ParticipationElements
    // Also change addTodo and removeTodo accordingly

    List<Ref> todos = []

    void addTodo( Ref todo ) {
        todos.add( todo )
        changed( "todos" )
    }

    void removeTodo( Ref todo ) {
        todos.remove( todo )
        changed( "todos" )
    }

}