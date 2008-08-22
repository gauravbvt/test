package com.mindalliance.channels.playbook.ifm.playbook

import com.mindalliance.channels.playbook.ifm.info.Information
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ref.impl.ComputedRef
import com.mindalliance.channels.playbook.ifm.taxonomy.EventType
import com.mindalliance.channels.playbook.mem.NoSessionCategory

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 17, 2008
 * Time: 1:36:56 PM
 */
class Detection extends InformationAct {

    Information information = new Information()

    boolean hasInformation() {
        return true
    }

    void setInformation(Information information) { // make sure actors are the only source
        this.@information = information
        information.sourceAgents = actors
        this.propertyChanged('information', null, information)
    }

    void setActors(List<Ref> agents) {
        super.setActors(agents)
        information.sourceAgents = agents
        this.propertyChanged('actors', null, actors)
        this.propertyChanged('information', null, information)
    }

    // Return implied event type
    static Ref implicitEventType() {
        return ComputedRef.from(Detection.class, 'makeImplicitEventType')
    }

    static EventType makeImplicitEventType() {
        EventType eventType = new EventType(name: 'detection',              // note: model is null
                description: 'A detection of an event',
                topics: ['information'])
        use(NoSessionCategory) {eventType.narrow(InformationAct.implicitEventType())}; // setting state of a computed ref
        return eventType
    }

    List<String> contentsAboutTopic(String topic) {
        switch (topic) {
            case 'information': return [information.about()]
            default: return super.contentsAboutTopic(topic)
        }
    }


}