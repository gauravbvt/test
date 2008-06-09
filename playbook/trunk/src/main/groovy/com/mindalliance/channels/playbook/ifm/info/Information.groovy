package com.mindalliance.channels.playbook.ifm.info

import com.mindalliance.channels.playbook.ref.Ref
import groovy.time.Duration
import com.mindalliance.channels.playbook.ifm.IfmElement

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Mar 29, 2008
* Time: 9:04:20 AM
*/
class Information extends AbstractInformation {  // the communicated (partial) account of an event (classified and attributed)

    Ref event // about an event description     -- required
    List<Ref> eventTypes = [] // classification of the event
    // EOIs are inherited
    List<Ref> sourceAgents = [] // sources of the information

    String toString() {
        return "About ${event.toString()}" // TODO - do better than this
    }

}