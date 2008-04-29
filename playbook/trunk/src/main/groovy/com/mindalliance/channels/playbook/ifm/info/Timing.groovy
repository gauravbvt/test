package com.mindalliance.channels.playbook.ifm.info

import com.mindalliance.channels.playbook.ref.impl.BeanImpl
import org.joda.time.Duration

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 28, 2008
 * Time: 7:33:29 PM
 */
class Timing extends BeanImpl {

    static final List<String> units = ['seconds', 'minutes', 'hours', 'days', 'weeks']

    int amount = 0
    String unit = 'seconds'
    Duration duration

    @Override
    List<String> transientProperties() {
        return (List<String>)(super.transientProperties() + ['duration', 'units'])
    }

    void detach() {
        duration = null
    }

    Duration getDuration() {
        if (!duration) duration = new Duration(amount * unitValue() * 1000)
        return duration
    }

    long unitValue() {
        switch(unit) {
            case 'seconds': return 1
            case 'minutes': return 60
            case 'hours': return 60 * 60
            case 'days': return 24 * 60 * 60
            case 'weeks': return 7 * 24 * 60 * 60
            default: throw new IllegalArgumentException("Unknown time unit $s")
        }
    }
}