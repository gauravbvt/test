package com.mindalliance.channels.playbook.ifm.info

import com.mindalliance.channels.playbook.ref.impl.BeanImpl
import com.mindalliance.channels.playbook.matching.SemanticMatcher
import com.mindalliance.channels.playbook.support.Level

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Mar 29, 2008
* Time: 1:06:55 PM
*/
class ElementOfInformation extends BeanImpl {
    
    String topic = ''
    String content = ''

    // Semantic matching of EOIs
    boolean matches(ElementOfInformation eoi) {     // this matches eoi if same as eoi or more specific than eoi
        assert topic && eoi.topic  // an eoi must always have a topic
        if (topic != eoi.topic) {   // if topics are different they must be semantically very close
            Level level = SemanticMatcher.getInstance().semanticProximity(topic, eoi.topic)
            if (level < Level.HIGH) return false   // more demanding on topic match than content match
        }
        if (!eoi.content.isEmpty()) {
            if (!content.isEmpty()) {  // if both have content they must match pretty closely
                Level level = SemanticMatcher.getInstance().semanticProximity(content, eoi.content)
                if (level < Level.MEDIUM) return false
            }
            else {
                return false  // empty content always less specific than any content
            }
        } // if matched-against eoi content is empty then anything (including empty content) matches it
        return true
    }
}