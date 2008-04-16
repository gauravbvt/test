package com.mindalliance.channels.playbook.ifm.context.model

import com.mindalliance.channels.playbook.ifm.IfmElement
import com.mindalliance.channels.playbook.ref.Ref

/**
* ...
*/
class Model extends IfmElement {

    String name
    Boolean shared

    List<Ref> participations = []
    List<Ref> elements = []

    String toString() {
        name
    }

    Boolean isAnalyst( Ref user ) {
        participations.each {
            if ( it.user == user ) return true;
            }
        return false
    }

    /**
     * Return what model content an analyst can create.
     */
    static List<Class<?>> contentClasses() {
        [ Role.class, OrganizationType.class ]
    }

    /**
     * Return what system objects an analyst can create.
     */
    static List<Class<?>> analystClasses() {
        [ Model.class ]
    }
}