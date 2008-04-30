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

    List<Ref> eventTypes = []// the kinds of event (and/or)
    LocationSpec locationSpec // constraints on location of event
    List<Ref> relationshipTypes = [] // type of relationship to an observer of the event
    Timing timing // must have occurred in the last n hours, days etc.

}