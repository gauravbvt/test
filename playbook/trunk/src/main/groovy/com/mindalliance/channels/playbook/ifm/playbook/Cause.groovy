package com.mindalliance.channels.playbook.ifm.playbook

import com.mindalliance.channels.playbook.ref.impl.BeanImpl
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.Timing
import com.mindalliance.channels.playbook.ifm.Defineable

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 16, 2008
 * Time: 11:53:53 AM
 */
class Cause extends BeanImpl implements Defineable {

    Ref trigger // another Event
    Timing delay = new Timing(amount:0)

    @Override
    List<String> transientProperties() {
        return (List<String>)(super.transientProperties() + ['known', 'defined'])
    }

    boolean isKnown() {
        return trigger as boolean
    }

    String toString() {
        return trigger as boolean ? trigger.deref().toString() : "" ;
    }

    public boolean isDefined() {
        return trigger as boolean && delay.isDefined()
    }
}