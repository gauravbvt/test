package com.mindalliance.channels.playbook.ifm.project.environment

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.project.ProjectElement
import com.mindalliance.channels.playbook.ifm.Named

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Apr 17, 2008
* Time: 10:45:25 AM
*/
class Relationship extends ProjectElement implements Named {

    Ref fromAgent
    Ref toAgent
    String name = ''
    Ref reverseRelationship // if any

    static Relationship create(Ref fromAgent, String name, Ref toAgent) {
        return new Relationship(fromAgent: fromAgent, name: name, toAgent: toAgent)
    }

    void afterDelete() {
        if (reverseRelationship as boolean) {
            reverseRelationship.begin()
            reverseRelationship.delete()
        }
    }

}