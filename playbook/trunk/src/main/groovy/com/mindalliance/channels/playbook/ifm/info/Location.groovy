package com.mindalliance.channels.playbook.ifm.info

import com.mindalliance.channels.playbook.ref.impl.BeanImpl
import com.mindalliance.channels.playbook.ref.Ref

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 17, 2008
 * Time: 9:50:16 AM
 */
class Location extends BeanImpl {

    Ref place       // a place is a named locationInfo is given
    LocationInfo locationInfo  // or else an unnamed locationInfo is given (not both)
    PlaceInfo placeInfo  // and then place details  

    double getLongitude() {
        double longitude = -1.0
        if (place) {
            longitude = locationInfo.getLongitude()
        }
        else if (locationInfo) {
            longitude = locationInfo.getLongitude()
        }
        return longitude
    }

    double getLatitude() {
        double latitude = -1.0
        if (place) {
            latitude = locationInfo.getLatitude()
        }
        else if (locationInfo) {
            latitude = locationInfo.getLatitude()
        }
        return latitude
    }

    boolean hasLatLong() {
        boolean known = false
        if (place) {
            known = place.locationInfo.hasLatLong()
        }
        else if (locationInfo) {
            known = locationInfo.hasLatLong()
        }
        return known
    }
}