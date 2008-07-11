package com.mindalliance.channels.playbook.analysis.profile

import org.joda.time.Duration
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

    Ref playbook
    Ref agent           // of an agent
    InformationAct act // cause
    Duration start   // at t = time zero + start -- always set
    Duration end     // at t = time zero + end  -- set only if self-terminating

    protected ProfileElement() {}

    ProfileElement(InformationAct infoAct, Ref agent) {
        super()
        this.playbook = infoAct.playbook
        this.act = infoAct
        this.agent = agent
        this.start = infoAct.startTime()
    }

 /*   ProfileElement(Ref playbook, Ref Agent) {
        super()
        this.playbook = playbook
        this.agent = agent
        this.start = new Duration(0)          // starts at T = 0, no end
    }
*/
    ProfileElement (ProfileElement cause, Ref agent) {
        this.playbook = cause.playbook
        this.agent = agent
        this.act = cause.act
        this.start = cause.start
        this.end = cause.end // assumes that it won't outlast its cause
    }

    String toString() {
        return super.toString() + " of $agent [${Timing.asString(start)}, ${end ? Timing.asString(end): ''}]" 
    }

    boolean isTimeLimited() {
        return end != null
    }

}