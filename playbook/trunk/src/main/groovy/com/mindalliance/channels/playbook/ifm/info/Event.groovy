package com.mindalliance.channels.playbook.ifm.info

import com.mindalliance.channels.playbook.ref.impl.BeanImpl
import com.mindalliance.channels.playbook.ref.Ref
import org.joda.time.Duration
import com.mindalliance.channels.playbook.ifm.info.Location

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Apr 17, 2008
* Time: 9:24:42 AM
*/
class Event extends BeanImpl {

    Ref eventType
    Ref cause // an Information Act, if any
    Duration when = Duration.ZERO // delay from start of cause, else from "time zero" (set by playbook)
    Location where
    Duration duration = Duration.ZERO // default is "forever"
    RelationshipSpec relationshipToCause // of knowing resource to information act

}