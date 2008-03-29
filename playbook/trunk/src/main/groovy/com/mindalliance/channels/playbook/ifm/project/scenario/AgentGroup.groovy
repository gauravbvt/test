package com.mindalliance.channels.playbook.ifm.project.scenario

import com.mindalliance.channels.playbook.ref.Ref

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Mar 21, 2008
* Time: 12:04:47 PM
*/
class AgentGroup extends Agent {

    List<Ref> agents = []

    boolean isIndividual() {
        return false
    }

}