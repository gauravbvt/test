package com.mindalliance.channels.playbook.ifm.info

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.Timing
import com.mindalliance.channels.playbook.ifm.Defineable

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Mar 29, 2008
* Time: 9:04:20 AM
*/
class Information extends AbstractInformation implements Defineable {  // the communicated (partial) account of an event (classified and attributed)

    boolean affirmed = true // else negated
    Ref event // about an event description     -- required
    List<Ref> eventTypes = [] // classification of the event
    // EOIs are inherited
    List<Ref> sourceAgents = [] // divulged sources of the information 
    Timing timeToLive = new Timing(amount: 0) // ttl of 0 means indefinite. Once expired, the info is no longer supported by its source(s)

    @Override
    List<String> transientProperties() {
        return (List<String>)(super.transientProperties() + ['defined'])
    }

    String toString() {
        if (!event as boolean) {
            return "About nothing"
        }
        else {
            return "About ${event.deref().toString()}" // TODO - do better than this?
        }
    }

    boolean isDefined() {
        return event as boolean
    }

    String makeLabel(int maxWidth) {
        String eventString = event as boolean ? event.deref().toString() : 'UNDEFINED'
        String label = "About\n ${eventString[0..Math.min(eventString.size()-1, maxWidth-1)]}"
        eventDetails.each {eoi ->
            label += "|${eoi.topic[0..Math.min(eoi.topic.size()-1, maxWidth-1)]}"
        }
        return label
    }

    boolean redundantOf(Information information) { // true if this adds nothing new to compared-to information, irrespective of sources and time to live
        if (!isDefined() || !information.isDefined()) return false
        if (event != information.event) return false
        // if not all event types are implied by an event type in the other information, then it does not repeat other information
        if (!eventTypes.all {et -> information.eventTypes.any {iet -> iet.implies(et)}}) return false
        // if not all eois match an eoi in other information, then does not repeat
        if (!eventDetails.all {eoi -> information.eventDetails.any {ieoi -> eoi.matches(ieoi)}}) return false
        return true
    }

}