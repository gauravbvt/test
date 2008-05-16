package com.mindalliance.channels.playbook.ifm.project.environment

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.project.ProjectElement

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Apr 17, 2008
* Time: 10:45:25 AM
*/
class Relationship extends ProjectElement {

    Ref fromAgent
    Ref toAgent
    String relationshipName = ''
    Ref reverseRelationship // if any

    void afterDelete() {
        if (reverseRelationship.exists()) {
            reverseRelationship.begin()
            reverseRelationship.delete()
        }
    }

}