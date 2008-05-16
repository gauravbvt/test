package com.mindalliance.channels.playbook.ifm.playbook

import com.mindalliance.channels.playbook.ref.Ref
import org.joda.time.Duration
import com.mindalliance.channels.playbook.ifm.Describable

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 16, 2008
 * Time: 12:49:38 PM
 */
/*abstract*/ class Occurrence extends PlaybookElement implements Causable, Describable {

    String description = ''
    Cause cause = new Cause()

    boolean isAfter(Ref causable) {
        if (hasTransitiveCause(causable)) return true
        return (causable.startTime() > this.startTime())
    }

    boolean hasTransitiveCause(Ref causable) {     // is act a direct or indirect cause of this occurrence
        if (!cause.isKnown()) return false
        if (cause.trigger == causable) return true
        if (cause.trigger.hasTransitiveCause(causable)) return true
        return false
    }

    Duration startTime() {
       Duration startTime = Duration.ZERO;
       if (cause.isKnown()) {
          startTime = cause.trigger.startTime() + cause.delay.duration
       }
       else {
          startTime = cause.delay.duration
       }
       return startTime;
    }

}