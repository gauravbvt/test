package com.mindalliance.channels.playbook.ifm.info

import com.mindalliance.channels.playbook.ref.impl.BeanImpl

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 21, 2008
 * Time: 3:19:40 PM
 */
class LatLong extends BeanImpl {

    Double latitude
    Double longitude

    @Override
    List<String> transientProperties() {
        return super.transientProperties() + ['set']
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