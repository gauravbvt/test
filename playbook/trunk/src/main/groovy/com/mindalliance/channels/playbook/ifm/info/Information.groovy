package com.mindalliance.channels.playbook.ifm.info

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.Timing
import com.mindalliance.channels.playbook.ifm.Defineable
import org.apache.log4j.Logger
import com.mindalliance.channels.playbook.support.RefUtils

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
            return "info about ?"
        }
        else {
            return "info about ${event.deref().toString()}" 
        }
    }

    boolean isDefined() {
        return event as boolean
    }

    String makeLabel(int maxWidth) {
        String eventString = event as boolean ? event.deref().toString() : 'UNDEFINED'
        String label = RefUtils.summarize("About\n $eventString", maxWidth)
        eventDetails.each {eoi ->
            label += "|${RefUtils.summarize(eoi.topic, maxWidth)}"
        }
        return label
    }

    boolean isComprisedIn(Information information) { // true if this adds nothing new to compared-to information, irrespective of sources and time to live
        if (!isDefined() || !information.isDefined()) return false
        if (event != information.event) return false // about same event
        // if not all event types are implied by an event type in the other information, then it does not repeat other information
        if (!eventTypes.all {et -> information.eventTypes.any {iet -> iet.implies(et)}}) return false
        // if not all eois match an eoi in other information, then does not repeat
        if (!eventDetails.all {eoi -> information.eventDetails.any {ieoi -> eoi.matches(ieoi)}}) return false
        return true
    }

    Information combineWith(Information information) {
        Information combined = new Information()
        if (isDefined() && information.isDefined()) {
            if (event == information.event) {
                combined.event = event
                // keep most narrow event types from both
                combined.eventTypes.addAll(eventTypes.findAll{et -> !information.eventTypes.any{iet -> iet.implies(et)}})
                combined.eventTypes.addAll(information.eventTypes.findAll{iet -> !eventTypes.any{et -> et.implies(iet)}})
                // combine all self' eois leaving out redundant ones
                combined.eventDetails.addAll(eventDetails.findAll{eoi -> !eoi.content.isEmpty() ||
                                                                         !information.eventDetails.any{ieoi -> ieoi.topic == eoi.topic  && !ieoi.content.isEmpty()}
                                                                  })
                combined.eventDetails.addAll(information.eventDetails.findAll{ieoi -> !ieoi.content.isEmpty() ||
                                                                               !eventDetails.any{eoi -> eoi.topic == ieoi.topic}
                                                                        })
             }
        }
        if (!combined.isDefined()) Logger.getLogger(this.class).warn("Combining incompatible information: ${this.toString()} + $information")
        return combined
    }

}