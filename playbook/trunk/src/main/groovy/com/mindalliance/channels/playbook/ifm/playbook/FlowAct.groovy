package com.mindalliance.channels.playbook.ifm.playbook

import com.mindalliance.channels.playbook.ref.Ref

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Apr 17, 2008
* Time: 1:41:27 PM
*/
class FlowAct extends InformationAct {

    Ref targetAgent

    @Override
    boolean isFlowAct() {
        return true
    }

    static List<String> impliedEventTypeTopics() {
        return InformationAct.class.impliedEventTypeTopics() + ['target']
    }

    List<String> contentsAboutTopic(String topic) {
        switch (topic) {
            case 'target': return [targetAgent.about()]
            default: return super.contentsAboutTopic(topic)
        }
    }



}