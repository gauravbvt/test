package com.mindalliance.channels.playbook.ref.impl

import com.mindalliance.channels.playbook.ref.Referenceable
import com.mindalliance.channels.playbook.ref.Reference
/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Mar 19, 2008
* Time: 8:49:28 AM
*/
abstract class ReferenceableImpl implements Referenceable {

    String id
    String db

    String getId() {
        return id ?: (id = makeGuid())   // If no id is given, make one
    }

    Reference getReference() {
        return new ReferenceImpl(id: getId(), db: getDb())
    }

    private String makeGuid() {
        String uuid = "${UUID.randomUUID()}"
        return uuid
    }


}