package com.mindalliance.channels.playbook.ifm.spec

import com.mindalliance.channels.playbook.ref.impl.BeanImpl
import com.mindalliance.channels.playbook.ref.Bean
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.IfmElement
import com.mindalliance.channels.playbook.ifm.playbook.InformationAct

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 10, 2008
 * Time: 3:15:11 PM
 */
/* abstract */ class SpecImpl extends BeanImpl implements Spec {

    protected List<String> transientProperties() {
        return super.transientProperties() + ['defined']
    }

     boolean matches(Bean bean) {     // DEFAULT
        if (!isDefined()) return true
        return doesMatch(bean)
    }

    boolean matchesAsOf(Ref el, Ref informationAct) {
        if (!isDefined()) return true
        IfmElement element = (IfmElement)element.deref()
        InformationAct act = (InformationAct)informationAct.deref()
        return doesMatchAsOf(element, act);
     }

    boolean narrows(Spec spec) {
        return false;  // Default
    }

    boolean isDefined() {
        return false;  // Default
    }

    boolean doesMatch(Bean bean) {
        throw new Exception("Context-indepedent matching not applicable") // DEFAULT
    }

    boolean doesMatchAsOf(IfmElement element, InformationAct act) {
        throw new Exception("Context-depedent matching not applicable") // DEFAULT
    }
     
}