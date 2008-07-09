package com.mindalliance.channels.playbook.analysis.profile

import com.mindalliance.channels.playbook.ifm.info.InformationNeed
import com.mindalliance.channels.playbook.ifm.playbook.InformationAct
import com.mindalliance.channels.playbook.ref.Ref

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 7, 2008
 * Time: 8:02:55 PM
 */
class NeedToKnow extends ProfileElement {

    InformationNeed informationNeed

    NeedToKnow(InformationAct act, Ref agent, InformationNeed informationNeed) {
        super(act, agent)
        this.informationNeed = informationNeed
    }

}