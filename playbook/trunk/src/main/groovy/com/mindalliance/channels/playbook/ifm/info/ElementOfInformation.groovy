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
    boolean matches(ElementOfInformation eoi) {
        SemanticMatcher matcher = SemanticMatcher.getInstance()
        // matching topic?
        Level level = matcher.semanticProximity(topic, eoi.topic)
        if (level < Level.HIGH) return false   // more demanding on topic match than content match
        // matching content?
        level = matcher.semanticProximity(content, eoi.content)
        if (level < Level.MEDIUM) return false
        return true
    }
}