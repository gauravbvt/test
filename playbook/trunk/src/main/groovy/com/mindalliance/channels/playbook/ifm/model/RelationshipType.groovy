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

    Ref inverseRelationship
    boolean transitive = false

}