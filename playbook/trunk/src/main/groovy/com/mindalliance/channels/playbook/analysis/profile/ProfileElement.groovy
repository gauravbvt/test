package com.mindalliance.channels.playbook.analysis.profile

import org.joda.time.Duration
import com.mindalliance.channels.playbook.ifm.Agent
import com.mindalliance.channels.playbook.ifm.playbook.Playbook
import com.mindalliance.channels.playbook.analysis.AnalysisElement
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.playbook.InformationAct

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 7, 2008
 * Time: 7:26:31 PM
 */
class ProfileElement extends AnalysisElement {

    Playbook playbook
    Agent agent           // of an agent
    Duration start   // at t = time zero + start
    Duration end     // at t = time zero + end

    protected ProfileElement() {}

    ProfileElement(Playbook playbook, Agent agent, Duration start, Duration end) {
        super()
        this.playbook = playbook
        this.agent = agent
        this.start = start
        this.end = end
    }

    ProfileElement(InformationAct act, Ref agent) {
        super()
        this.playbook = (Playbook)act.playbook.deref()
        this.agent = (Agent)agent.deref()
        this.start = act.startTime()
    }

    String shortClassName() {// Default
        String cn = this.class.name
        String name = "${cn.substring(cn.lastIndexOf('.') + 1)}"
        return name
    }

    String toString() {
        return "${shortClassName()} by $agent" 
    }
}