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

    EventSpec eventSpec = new EventSpec() // about what kind of event
    AgentSpec sourceAgentSpec = new AgentSpec() // from whom is this kind of information credible

    @Override
    List<String> transientProperties() {
        return super.transientProperties() + ['name']
    }


    String getName() {
        return toString()
    }

    String toString() {
        String s = "Information about "
        if (eventSpec) {
            eventSpec.eventTypes.each {et ->
                s += "${et.name},"
            }
        }
        return s.substring(0, s.size()-1)
    }

}