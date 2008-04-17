package com.mindalliance.channels.playbook.ifm.info

import com.mindalliance.channels.playbook.ref.impl.BeanImpl
import com.mindalliance.channels.playbook.ref.Ref
import org.joda.time.Duration

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Apr 17, 2008
* Time: 9:19:42 AM
*/
class EventSpec extends BeanImpl {

    Ref eventType // the kind of event
    LocationSpec locationSpec // constraints on location of event
    RelationshipSpec relationshipSpec // type of relationship to the cause of the event
    Duration timing // must have occurred in the last n hours, days etc.

}