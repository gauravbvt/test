package com.mindalliance.channels.playbook.analysis.profile

import com.mindalliance.channels.playbook.ifm.info.Information
import com.mindalliance.channels.playbook.ifm.playbook.InformationAct
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.Agent

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 7, 2008
 * Time: 7:54:19 PM
 */
class Know extends ProfileElement {

    Information information
    List<Agent> sources = []

    Know(InformationAct act, Ref agent, Information information) {
        super(act, agent)
        this.information = information
        if (information.timeToLive.isDefined()) {
            end = start.plus(information.timeToLive.duration)
        }
        information.sourceAgents.each {ref ->
            Agent source = (Agent)ref.deref()
            if (source as boolean) sources.add(source)
        }
    }

}