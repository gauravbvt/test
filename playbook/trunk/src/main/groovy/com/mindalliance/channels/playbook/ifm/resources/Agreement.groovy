package com.mindalliance.channels.playbook.ifm.resources

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.info.InformationTemplate
import org.joda.time.Duration
import com.mindalliance.channels.playbook.ref.impl.BeanImpl

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Apr 17, 2008
* Time: 10:43:29 AM
*/
class Agreement extends BeanImpl {

    static final List<String> DELIVERIES = ['push', 'pull']

    Ref withResource
    InformationTemplate informationCovered
    String delivery = 'push' // one of {push,pull}
    Duration maxDelay = Duration.ZERO
    boolean effective = false // whether the agreement is in place in real life

}