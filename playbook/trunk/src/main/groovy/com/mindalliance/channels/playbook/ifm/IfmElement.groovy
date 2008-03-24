package com.mindalliance.channels.playbook.ifm

import com.mindalliance.channels.playbook.ref.impl.ReferenceableImpl
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.mem.ApplicationMemory

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Mar 19, 2008
* Time: 12:36:45 PM
*/
/*abstract*/ class IfmElement extends ReferenceableImpl implements Serializable {

    Date createdOn = new Date()

    void makeRoot() {
        Ref root = ApplicationMemory.ROOT
        this.id = root.id
        this.db = root.db
    }

}