package com.mindalliance.channels.playbook.ifm.playbook

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.info.Timing

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Apr 17, 2008
* Time: 1:27:48 PM
*/
/* abstract*/ class InformationAct extends Occurrence {

    Ref actorAgent // an agent, i.e. group or team within the scope of the Playbook, or a resource in the scope of the Playbook's Project

    @Override
    List<String> transientProperties() {
        return super.transientProperties() + ['duration']
    }

    boolean isFlowAct() {
        return false
    }

    boolean isSharingAct() {
        return false
    }

    Timing getDuration() {
        return new Timing(amount:0)
    }

}