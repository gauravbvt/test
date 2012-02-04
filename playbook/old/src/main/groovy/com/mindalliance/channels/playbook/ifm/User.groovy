package com.mindalliance.channels.playbook.ifm

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ref.Referenceable

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Mar 21, 2008
* Time: 11:34:02 AM
*/
class User extends ModelElement {

    String name = 'No name'
    String userId
    String password
    boolean admin
    boolean analyst
    boolean manager

    List<Ref> tabs = []
    Ref selectedTab

    // Selections
    Ref selectedProject
    Ref selectedTaxonomy

    String toString() { name }

    void addTab( Ref tab ) {
        tabs.add( tab );
        changed( "tabs" );
    }

    void removeTab( Ref tab ) {
        tabs.remove( tab );
        changed( "tabs" );
    }

    static List contentClasses() {
        [ Tab.class ]
    }

    Set hiddenProperties() {
        return (super.hiddenProperties() + ['password']) as Set
    }
}