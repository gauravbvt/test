package com.mindalliance.channels.playbook.ifm.playbook

import com.mindalliance.channels.playbook.ifm.info.Information
import com.mindalliance.channels.playbook.ref.Ref

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Apr 17, 2008
* Time: 1:36:56 PM
*/
class Detection extends InformationAct {

    Information information = new Information()

    boolean hasInformation() {
        return true
    }

    void setActorAgent(Ref agent) {  // the source of detected information is always the actor
        super.setActorAgent(agent)
        information.setSourceAgents([agent])
    }


}