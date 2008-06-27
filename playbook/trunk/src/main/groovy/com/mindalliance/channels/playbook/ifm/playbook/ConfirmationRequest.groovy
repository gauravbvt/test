package com.mindalliance.channels.playbook.ifm.playbook

import com.mindalliance.channels.playbook.ifm.spec.AgentSpec
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ref.impl.ComputedRef
import com.mindalliance.channels.playbook.ifm.model.EventType
import com.mindalliance.channels.playbook.mem.NoSessionCategory

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 17, 2008
 * Time: 1:39:42 PM
 */
class ConfirmationRequest extends SharingAct {

    AgentSpec sourceSpec = new AgentSpec() // source is specified...
    Ref sourceAgent // xor is identified

    @Override
    List<String> transientProperties() {
        return (List<String>) (super.transientProperties() + ['sourceSpecified'])
    }

    void setSourceSpec(AgentSpec agentSpec) {
        AgentSpec old = sourceSpec
        if (agentSpec.isDefined()) {
            sourceAgent = null
        }
        propertyChanged("sourceSpec", old, agentSpec)
    }

    void setSourceAgent(Ref agent) {
        Ref old = sourceAgent
        if (agent as boolean) {
            sourceAgent = agent
            sourceSpec = new AgentSpec()
        }
        propertyChanged("sourceAgent", old, agent)
    }

    boolean isSourceSpecified() {
        return !sourceAgent as boolean
    }

    // Return implied event type
    static Ref impliedEventType() {
        return ComputedRef.from(Assignation.class, 'makeImpliedEventType')
    }

    static EventType makeImpliedEventType() {
        EventType eventType = new EventType(name: 'confirmation request',              // note: model is null
                description: 'A confirmation request',
                topics: impliedEventTypeTopics())
        use(NoSessionCategory) {eventType.narrow(Event.class.impliedEventType())}; // setting state of a computed ref
        return eventType
    }


    static List<String> impliedEventTypeTopics() {
        return SharingAct.class.impliedEventTypeTopics() + ['source']
    }

    List<String> contentsAboutTopic(String topic) {
        switch (topic) {
            case 'source': if (isSourceSpecified()) {
                                return [sourceSpec.about()]
                            }
                            else {
                                return [sourceAgent.about()]
                            }
            default: return super.contentsAboutTopic(topic)
        }
    }


}