package com.mindalliance.channels.playbook.ifm.context.model

import com.mindalliance.channels.playbook.ref.impl.BeanImpl
import com.mindalliance.channels.playbook.ref.Ref
import org.joda.time.Duration

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Mar 29, 2008
* Time: 8:46:54 AM
*/
class Responsibility extends BeanImpl {
    List<Ref> informationSpecifications = [] // what information role ought to be aware of
    List<Ref> informationNeeds = [] // what information needs role ought to be aware of
    List<Ref> assignments = [] // what assignments role ought to be aware of
    Ref responseTask // response, if any, when all of the above known
    Duration maximumResponseDelay = new Duration(0) // default is immediately
}