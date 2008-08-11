package com.mindalliance.channels.playbook.ifm

import com.mindalliance.channels.playbook.ref.impl.BeanImpl
import org.joda.time.Duration

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 28, 2008
 * Time: 7:33:29 PM
 */
class Timing extends BeanImpl implements Defineable {
    private static final long serialVersionUID = -1L;

    static final List<String> units = ['seconds', 'minutes', 'hours', 'days', 'weeks']

    Integer amount = 0
    String unit = 'seconds'
    Duration duration

    @Override
    List<String> transientProperties() {
        return (List<String>)(super.transientProperties() + ['duration', 'units', 'defined'])
    }

    boolean isDefined() {
        return amount != 0
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

    String toString() {
        return Timing.asString(getDuration())
    }

    static String asString(Duration duration) {
        long seconds = duration.millis / 1000 as long
        long minutes = seconds / 60 as long
        long hours = minutes / 60 as long
        long days = hours / 24 as long
        long weeks = days / 7 as long
        String text = ""
        if (weeks) text += "${weeks}w"
        if (days % 7) {
            if (text) text += " "
            text += "${days % 7}d"
        }
        if (hours % 24) {
            if (text) text += " "
            text += "${hours % 24}h"
        }
        if (minutes % 60) {
            if (text) text += " "
            text += "${minutes % 60}m"
        }
        if (seconds % 60) {
            if (text) text += " "
            text += "${seconds % 60}s"
        }
        if (!text) {
            text = "Start"
        }
        else {
            text = "+$text"
        }
        return text
    }

    boolean isShorterOrEqualTo(Timing other) {
        return !other.duration.isLongerThan(this.duration)
    }
}