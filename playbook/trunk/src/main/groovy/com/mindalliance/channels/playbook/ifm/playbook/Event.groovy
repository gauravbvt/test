package com.mindalliance.channels.playbook.ifm.playbook

import com.mindalliance.channels.playbook.ifm.info.Location
import com.mindalliance.channels.playbook.ifm.Described
import com.mindalliance.channels.playbook.ref.Ref
import org.joda.time.Duration
import com.mindalliance.channels.playbook.ref.impl.ComputedRef
import com.mindalliance.channels.playbook.ifm.model.EventType
import com.mindalliance.channels.playbook.ifm.model.PlaybookModel
import com.mindalliance.channels.playbook.ifm.Named

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 17, 2008
 * Time: 9:24:42 AM
 */
// Something of consequence becomes true somewhere at some point in time
// How it is classified and accounted is the point of views of individual agents
// that they may share with others fully or partially, dispute and confirm
class Event extends PlaybookElement implements Named, Described {

    String name = ''
    String description = ''
    Cause cause = new Cause()
    Location location = new Location()

    String toString() {
        return name
    }

    boolean isAfter(Ref event) {
        if (hasTransitiveCause(event)) return true
        return (event.startTime() > this.startTime())
    }

    boolean hasTransitiveCause(Ref event) {     // is act a direct or indirect cause of this occurrence
        if (!cause.isKnown()) return false
        if (cause.trigger == event) return true
        if (cause.trigger.hasTransitiveCause(event)) return true
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

    // Return event types implied by the event (provides for reflexion on events, including information acts)
    static Ref impliedEventType() {
        return ComputedRef.from(Event.class, 'makeEventTypeEvent')
    }

    static EventType makeEventTypeEvent() {
        return new EventType(name:'event',              // note: model is null
                             description:'An event of some kind',
                             topics: ['description', 'location', 'cause'])
    }

}