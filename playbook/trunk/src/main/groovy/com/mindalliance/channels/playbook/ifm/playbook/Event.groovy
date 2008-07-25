package com.mindalliance.channels.playbook.ifm.playbook

import com.mindalliance.channels.playbook.ifm.info.Location
import com.mindalliance.channels.playbook.ifm.Described
import com.mindalliance.channels.playbook.ref.Ref
import org.joda.time.Duration
import com.mindalliance.channels.playbook.ref.impl.ComputedRef
import com.mindalliance.channels.playbook.ifm.taxonomy.EventType
import com.mindalliance.channels.playbook.ifm.Named
import com.mindalliance.channels.playbook.ifm.Timing
import com.mindalliance.channels.playbook.ifm.info.Information
import com.mindalliance.channels.playbook.ifm.info.ElementOfInformation
import com.mindalliance.channels.playbook.ifm.info.Risk

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

    static private List<Class<? extends Event>> EventClasses = [
            Event.class, InformationAct.class, FlowAct.class, SharingAct.class, Assignation.class, Association.class,
            ConfirmationRequest.class, Detection.class, InformationRequest.class, InformationTransfer.class,
            Relocation.class, SharingCommitment.class, SharingRequest.class, Task.class
    ]

    static private List<Ref> implicitEventTypes

    String name = ''
    String description = ''
    Cause cause = new Cause()
    Location location = new Location()
    Risk risk = new Risk()


    @Override
    List<String> transientProperties() {
        return (List<String>) (super.transientProperties() + ['informationAct', 'implicitEventType', 'eventClasses', 'implicitEventTypes'])
    }

    Set keyProperties() {
        return (super.keyProperties() + ['name', 'description']) as Set
    }

    String toString() {
        return name ?: "Unnamed"
    }

    boolean isInformationAct() {
        return false
    }

    boolean isAfter(Ref event) {
        if (hasTransitiveCause(event)) return true
        return this.startTime() > event.startTime()
    }

    boolean hasTransitiveCause(Ref event) {     // is act a direct or indirect cause of this occurrence
        if (!cause.isKnown()) return false
        if (cause.trigger == event) return true
        if (cause.trigger.hasTransitiveCause(event)) return true
        return false
    }

    Duration startTime() {
        Duration startTime
        if (cause.isKnown()) {
            startTime = cause.trigger.startTime() + cause.delay.duration
        }
        else {
            startTime = cause.delay.duration
        }
        return startTime
    }

    static List<Ref> findAllImplicitEventTypes() {
        if (implicitEventTypes == null) {
            implicitEventTypes = []
            EventClasses.each {clazz ->
                Ref implicit = clazz.implicitEventType()
                implicitEventTypes.add(implicit)
            }
        }
        return implicitEventTypes
    }

    // Return event type implied by the event (provides for reflexion on events, including information acts)
    static Ref implicitEventType() {
        return ComputedRef.from(Event.class, 'makeImplicitEventType')
    }

    static EventType makeImplicitEventType() {
        EventType eventType = new EventType(name: 'event',              // note: model is null
                description: 'An event of some kind',
                topics: ['start time', 'description', 'location', 'cause'])
        return eventType
    }

    Ref getImplicitEventType() {
        return Event.implicitEventType()
    }

    List<String> contentsAboutTopic(String topic) {
        switch (topic) {
            case 'start time': return [Timing.asString(startTime())]
            case 'description': return [description]
            case 'location': return [location.about()]
            case 'cause': return [cause.about()]
            default: return []
        }
    }

    // Create information about this event
    Information makeInformation() {
        Information info = new Information(event: this.reference)
        Ref eventType = this.class.implicitEventType()
        info.eventTypes.add(eventType)
        eventType.allTopics().each {topic ->
            List<String> contents = contentsAboutTopic(topic)
            contents.each {content ->
                ElementOfInformation eoi = new ElementOfInformation(topic: topic, content: content)
                info.eventDetails.add(eoi)
            }
        }
        return info
    }

    // QUERIES

    List<Ref> findAllInformationActsCausedByEvent() {
        assert playbook as boolean
        Playbook playbook = (Playbook) this.playbook.deref()
        return (List<Ref>) playbook.informationActs.findAll {act ->
            act as boolean && act.cause.trigger == this.reference
        }
    }

    List<Ref> findAllEventsCausedByEvent() {
        assert playbook as boolean
        Playbook playbook = (Playbook) this.playbook.deref()
        return (List<Ref>) playbook.events.findAll {event ->
            event as boolean && event.cause.trigger == this.reference
        }
    }

    List<Ref> findAllInformationActsAboutEvent() {
        assert playbook as boolean
        return (List<Ref>) this.playbook.informationActs.findAll {act ->
            act as boolean && act.hasInformation() && act.information.event == this.reference
        }
    }

    List<Ref> findAllPriorEvents() {
        assert playbook as boolean
        return (List<Ref>) this.playbook.events.findAll {event -> event as boolean && this.isAfter(event) }
    }

    List<Ref> findAllPriorOccurrences() {
        return (List<Ref>) this.playbook.findAllPriorOccurrencesOf(this.reference)
    }
    // END QUERIES

}