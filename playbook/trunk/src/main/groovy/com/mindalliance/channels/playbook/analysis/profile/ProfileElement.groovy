package com.mindalliance.channels.playbook.analysis.profile

import org.joda.time.Duration
import com.mindalliance.channels.playbook.ifm.Agent
import com.mindalliance.channels.playbook.ifm.playbook.Playbook
import com.mindalliance.channels.playbook.analysis.AnalysisElement
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.playbook.InformationAct
import com.mindalliance.channels.playbook.ifm.Timing

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
    Duration start   // at t = time zero + start -- always set
    Duration end     // at t = time zero + end  -- set only if self-terminating

    protected ProfileElement() {}

    ProfileElement(InformationAct act, Ref agent) {
        super()
        this.playbook = (Playbook)act.playbook.deref()
        this.agent = agent ? (Agent)agent.deref() : null
        this.start = act.startTime()
    }

    String toString() {
        return super.toString() + " of $agent [${Timing.asString(start)}, ${end ? Timing.asString(end): ''}]" 
    }

    boolean isTimeLimited() {
        return end != null
    }

}