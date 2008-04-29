package com.mindalliance.channels.playbook.ifm.project

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.info.InformationTemplate
import org.joda.time.Duration
import com.mindalliance.channels.playbook.ifm.IfmElement
import com.mindalliance.channels.playbook.ifm.info.Timing
import com.mindalliance.channels.playbook.support.RefUtils

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Apr 17, 2008
* Time: 10:43:29 AM
*/
class Agreement extends IfmElement {

    static final List<String> DELIVERIES = ['notify', 'answer']
    
    String description = ''
    Ref fromResource
    Ref toResource
    InformationTemplate informationCovered
    String delivery = 'notify' // one of {push,pull}
    Timing maxDelay = new Timing(msecs:0)
    boolean effective = false // whether the agreement is in place in real life

    String toString() {
        return delivery
    }

}