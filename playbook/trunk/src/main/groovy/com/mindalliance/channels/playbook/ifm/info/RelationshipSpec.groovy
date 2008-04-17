package com.mindalliance.channels.playbook.ifm.info

import com.mindalliance.channels.playbook.ref.impl.BeanImpl
import com.mindalliance.channels.playbook.ref.Ref

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Apr 17, 2008
* Time: 9:22:23 AM
*/
class RelationshipSpec extends BeanImpl {

    boolean objectActor = true   // else it is the target if applicable
    Ref relationshipType   // type of relationship to element identified by the object
    
}