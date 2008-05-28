package com.mindalliance.channels.playbook.ifm.spec

import com.mindalliance.channels.playbook.ref.impl.BeanImpl
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.spec.Spec
import com.mindalliance.channels.playbook.ifm.spec.LocationSpec
import com.mindalliance.channels.playbook.ifm.Timing
import com.mindalliance.channels.playbook.ifm.Timing

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Apr 17, 2008
* Time: 9:19:42 AM
*/
class EventSpec extends BeanImpl implements Spec {

    List<Ref> eventTypes = []// the kinds of event (AND-ed)
    LocationSpec locationSpec = new LocationSpec() // constraints on location of event
    Timing timing = new Timing(amount:0)// must have occurred in the last n hours, days etc.

    @Override
    List<String> transientProperties() {
        return (List<String>)(super.transientProperties() + ['defined'])
    }

    boolean isDefined() {
        return !eventTypes.isEmpty()
    }

    public boolean matches(Ref element) {
        return false;  // TODO
    }

    public boolean narrows(Spec spec) {
        return false;  // TODO
    }
}