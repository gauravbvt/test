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

    static final List<String> units = ['Second', 'Minute', 'Hour', 'Day', 'Week']

    long msecs = 0
    String unit = 'Second'
    Duration duration

    @Override
    List<String> transientProperties() {
        return (List<String>)(super.transientProperties() + ['duration', 'units'])
    }

    void detach() {
        duration = null
    }

    Duration getDuration() {
        if (!duration) duration = new Duration(msecs * unitValue())
        return duration
    }

    long unitValue() {
        switch(unit) {
            case 'Second': return 1000
            case 'Minute': return 60 * 1000
            case 'Hour': return 60 * 60 * 1000
            case 'Day': return 24 * 60 * 60 * 1000
            case 'Week': return 7 * 24 * 60 * 60 * 1000
            default: throw new IllegalArgumentException("Unknown time unit $s")
        }
    }
}