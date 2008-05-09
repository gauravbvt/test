package com.mindalliance.channels.playbook.ifm.model

import com.mindalliance.channels.playbook.ref.Ref

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Apr 17, 2008
* Time: 10:50:29 AM
*/
class RelationshipType extends ElementType {

    static final List<String> resourceKinds = ["Person", "Organization", "Position", "System"]
    
    List<String> fromKinds = []  // in: Person, Organization, System, Position -- none = any
    List<String> toKinds = []
    boolean transitive = false

    @Override
    List<String> transientProperties() {
        return (List<String>)(super.transientProperties() + ['resourceKinds'])
    }


    boolean matchesTo(Ref resource) {
        return toKinds.contains(resource.type)
    }

    boolean matchesFrom(Ref resource) {
        return fromKinds.contains(resource.type)
    }

}