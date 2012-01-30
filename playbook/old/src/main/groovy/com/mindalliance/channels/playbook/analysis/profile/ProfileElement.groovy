package com.mindalliance.channels.playbook.analysis.profile

import org.joda.time.Duration
import com.mindalliance.channels.playbook.analysis.AnalysisElement
import com.mindalliance.channels.playbook.ifm.playbook.InformationAct
import com.mindalliance.channels.playbook.ifm.Timing
import com.mindalliance.channels.playbook.ref.Referenceable
import com.mindalliance.channels.playbook.ifm.playbook.PlaybookElement
import com.mindalliance.channels.playbook.ifm.playbook.Event
import com.mindalliance.channels.playbook.ifm.playbook.Playbook
import com.mindalliance.channels.playbook.ifm.Agent

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 7, 2008
 * Time: 7:26:31 PM
 */
abstract class ProfileElement extends AnalysisElement {

    Playbook playbook
    Agent agent           // of an agent
    Referenceable cause // cause
    Duration start = new Duration(0)   // at t = time zero + start -- always set
    Duration end     // at t = time zero + end  -- set only if self-terminating

    protected ProfileElement() {}

    ProfileElement(PlaybookElement cause, Agent agent) {
        super()
        this.playbook = (Playbook)cause.playbook.deref()
        this.cause = cause
        this.agent = agent
        if (cause instanceof Event) {
            this.start = ((Event)cause).startTime()
        }
    }

    ProfileElement(Playbook playbook, Referenceable cause, Agent Agent) {
        super()
        this.playbook = playbook
        this.cause = cause
        this.agent = agent
        this.start = new Duration(0)          // starts at T = 0, no end
    }

    ProfileElement (ProfileElement cause, Agent agent) {
        this.playbook = (Playbook)cause.playbook.deref()
        this.agent = agent
        this.start = cause.start
        this.end = cause.end // assumes that it won't outlast its cause
    }

    InformationAct getAct() {
        switch (cause) {
            case InformationAct.class: return (InformationAct)cause
            case ProfileElement.class: return ((ProfileElement)cause).getAct()
            default: return null
        }
    }

    String toString() {
        return super.toString() + " of $agent [${Timing.asString(start)}, ${end ? Timing.asString(end): ''}]" 
    }

    boolean isTimeLimited() {
        return end != null
    }

    boolean isForever() {
        return end == null
    }

}