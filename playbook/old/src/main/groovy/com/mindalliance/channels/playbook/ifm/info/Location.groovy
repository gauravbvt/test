package com.mindalliance.channels.playbook.ifm.info

import com.mindalliance.channels.playbook.ref.impl.BeanImpl
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.Defineable
import com.mindalliance.channels.playbook.ifm.Locatable

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 17, 2008
 * Time: 9:50:16 AM
 */
class Location extends BeanImpl implements Locatable, Defineable {

    Ref place       // a place is either a named location (possibly with a geoLocation)
    GeoLocation geoLocation = new GeoLocation()  // or else a geoLocation

    @Override
    List<String> transientProperties() {
        return (List<String>)(super.transientProperties() + ['location', 'longitude', 'latitude', 'effectiveGeoLocation', 'defined',
                                               'placeType', 'areaType', 'APlace', 'AGeoLocation'])
    }

    boolean isDefined() {
        return place as boolean || geoLocation.isDefined()
    }

    boolean isAPlace() {
        return place as boolean
    }

    boolean isAGeoLocation() {
        return !isAPlace()
    }

    Location getLocation() {
        return this
    }

    Ref getPlaceType() {
        if (place as boolean) {
            return place.placeType
        }
        else {
            return null
        }
    }

    Ref getAreaType() {
        if (place as boolean) {
            return null    // a place is not an area
        }
        else {
            return geoLocation.areaType
        }
    }

    String toString() {
        String s
        if (place as boolean) {
            s = "${place.name}"
        }
        else {
            s = "${geoLocation.toString()}"
        }
        return s
    }

    void setPlace(Ref place) {
        this.place = place
    }
    

    double getLongitude() {
        double longitude = -1.0
        if (place) {
            longitude = geoLocation.getLongitude()
        }
        else if (geoLocation) {
            longitude = geoLocation.getLongitude()
        }
        return longitude
    }

    double getLatitude() {
        double latitude = -1.0
        if (place) {
            latitude = geoLocation.getLatitude()
        }
        else if (geoLocation) {
            latitude = geoLocation.getLatitude()
        }
        return latitude
    }

    boolean hasLatLong() {
        boolean known = false
        if (place as boolean) {
            GeoLocation geoLocation = place.findGeoLocation()
            known = geoLocation && geoLocation.hasLatLong()
        }
        else if (geoLocation) {
            known = geoLocation.hasLatLong()
        }
        return known
    }

    boolean isWithin(Location other) {
        return this.effectiveGeoLocation.isWithin(other.effectiveGeoLocation)
    }

    boolean isNearby(Location other) {
        return this.effectiveGeoLocation.isNearby(other.effectiveGeoLocation)
    }

    boolean isSameAs(Location other) {
        return this.effectiveGeoLocation.isSameAs(other.effectiveGeoLocation)
    }

    GeoLocation getEffectiveGeoLocation() {
        if (place as boolean) return place.findGeoLocation()
        else return geoLocation
    }
}