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
    LocationInfo locationInfo = new LocationInfo()  // or else an unnamed locationInfo is given (not both)
    PlaceInfo placeInfo = new PlaceInfo()  // and then place details

    @Override
    List<String> transientProperties() {
        return super.transientProperties() + ['longitude', 'latitude', 'effectiveLocationInfo', 'defined']
    }

    boolean isDefined() {
        return place || locationInfo.isDefined()
    }

    String toString() {
        String s
        if (place) {
            s = "${place.name}"
        }
        else {
            s = "${locationInfo.toString()}"
        }
        if (!placeInfo.isEmpty()) {
            s += " (${placeInfo.toString()})"
        }
        return s
    }

    void setPlace(Ref place) {
        this.place = place
        placeInfo = new PlaceInfo() // reset place info
    }
    

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

    boolean isWithin(Location other) {
        return this.effectiveLocationInfo.isWithin(other.effectiveLocationInfo)
    }

    LocationInfo getEffectiveLocationInfo() {
        if (place) return place.locationInfo
        else return locationInfo
    }
}