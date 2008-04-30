package com.mindalliance.channels.playbook.ifm.info

import com.mindalliance.channels.playbook.ref.impl.BeanImpl
import org.joda.time.Duration
import com.mindalliance.channels.playbook.ref.Ref

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Apr 17, 2008
* Time: 9:18:11 AM
*/
class InformationTemplate extends AbstractInformation {

    EventSpec about // about what kind of event
    // Timing timeToLive = new Timing(msecs:0) // how long before this kind of information usually expires
    List<Ref> sourceOrganizationTypes // from whom is this kind of information credible

}