package com.mindalliance.channels.playbook.ifm

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ref.impl.BeanImpl

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
    boolean manager
    Ref person

    // TODO
    // make list computable from Todos as ParticipationElements
    // Also change addTodo and removeTodo accordingly

    List<Ref> todos = []

    List<Ref> tabs = []

    void addTodo( Ref todo ) {
        todos.add( todo )
        changed( "todos" )
    }

    void removeTodo( Ref todo ) {
        todos.remove( todo )
        changed( "todos" )
    }

    void addTab( Ref tab ) {
        tabs.add( tab );
        changed( "tabs" );
    }

    void removeTab( Ref tab ) {
        tabs.remove( tab );
        changed( "tabs" );
    }

    static List<Class<?>> contentClasses() {
        [ Todo.class, Tab.class ]
    }

}