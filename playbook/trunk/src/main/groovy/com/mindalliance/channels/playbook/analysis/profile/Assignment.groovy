package com.mindalliance.channels.playbook.analysis.profile

import com.mindalliance.channels.playbook.ifm.Responsibility
import com.mindalliance.channels.playbook.ifm.playbook.InformationAct
import com.mindalliance.channels.playbook.ref.Ref

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 7, 2008
 * Time: 8:07:26 PM
 */
class Assignment extends ProfileElement {

    Responsibility responsibility

    Assignment(InformationAct act, Ref agent, Responsibility responsibility) {
        super(act, agent)
        this.responsibility = responsibility
    }

}