package com.mindalliance.channels.playbook.analysis.profile

import com.mindalliance.channels.playbook.ifm.info.Information
import com.mindalliance.channels.playbook.ifm.Agent
import org.joda.time.Duration
import com.mindalliance.channels.playbook.ifm.playbook.Playbook
import com.mindalliance.channels.playbook.ifm.playbook.InformationAct
import com.mindalliance.channels.playbook.ref.Ref

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 7, 2008
 * Time: 7:54:19 PM
 */
class Know extends ProfileElement {

    Information information

    Know(Playbook playbook, Agent agent, Duration start, Duration end, Information information) {
        super(playbook, agent, start, end)
        this.information = information
    }

    Know(InformationAct act, Ref agent, Information information) {
       super(act, agent)
       this.information = information
    }

}