package com.mindalliance.channels.playbook.ifm.info

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ref.impl.BeanImpl
import org.joda.time.Duration

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Mar 29, 2008
* Time: 9:05:53 AM
*/
// Assignment of a Responsibility to any agent with a given role, possibly within a given type of organization
// and possiblylimited to some location
class Assignment extends BeanImpl {

    List<InformationTemplate> n2kInformation // what must be known if knowable -- required
    Ref mustDoTaskType // what to do, if anything, when all the above is known
    Duration maxReactionTime = Duration.ZERO // maximum reaction time -- defaults to "zero"
}