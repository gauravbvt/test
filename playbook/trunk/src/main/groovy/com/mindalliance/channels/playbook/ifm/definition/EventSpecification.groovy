package com.mindalliance.channels.playbook.ifm.definition

import com.mindalliance.channels.playbook.ifm.playbook.Event
import com.mindalliance.channels.playbook.ref.Bean

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 19, 2008
 * Time: 5:22:35 PM
 */
class EventSpecification extends Specification {

    public Class<? extends Bean> getMatchingDomainClass() {
        return Event.class
    }

}