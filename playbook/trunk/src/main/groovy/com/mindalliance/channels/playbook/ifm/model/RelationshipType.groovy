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

    static final List<String> resourceKinds = ["Resource","Organization", "Person", "Position", "System"]
    
    String fromKind = "Resource"  // one of: Resource, Person, Organization, System, Position
    String toKind = "Resource"
    boolean transitive = false

    @Override
    List<String> transientProperties() {
        return (List<String>)(super.transientProperties() + ['resourceKinds'])
    }


    boolean matchesTo(Ref resource) {
        return toKind == "Resource" || resource.type == toKind
    }

    boolean matchesFrom(Ref resource) {
        return fromKind == "Resource" || resource.type == fromKind
    }

}