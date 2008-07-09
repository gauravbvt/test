package com.mindalliance.channels.playbook.analysis.profile

import com.mindalliance.channels.playbook.ifm.playbook.InformationAct
import com.mindalliance.channels.playbook.ref.Ref

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 7, 2008
 * Time: 8:35:52 PM
 */
class Insight extends ProfileElement {   // an agent knows a profile element of another agent

    ProfileElement profileElement

    Insight(InformationAct act, Ref agent, ProfileElement profileElement) {
       super(act, agent)
       this.profileElement = profileElement
    }

}