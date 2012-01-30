package com.mindalliance.channels.playbook.ifm.info

import com.mindalliance.channels.playbook.ref.impl.BeanImpl
import com.mindalliance.channels.playbook.ifm.Defineable
import com.mindalliance.channels.playbook.ifm.Defineable

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 21, 2008
 * Time: 3:19:40 PM
 */
class LatLong extends BeanImpl implements Defineable {

    Double latitude
    Double longitude

    @Override
    List<String> transientProperties() {
        return super.transientProperties() + ['set', 'defined']
    }

    boolean isDefined() {
        return latitude && longitude
    }

    String toString() {
        String latitudeString = latitude ? "$latitude" : "?"
        String longitudeString = longitude ? "$longitude" : "?"
        return "latitude: $latitudeString, longitude: $longitudeString"
    }

    boolean isSet() {
        return latitude && longitude
    }

}