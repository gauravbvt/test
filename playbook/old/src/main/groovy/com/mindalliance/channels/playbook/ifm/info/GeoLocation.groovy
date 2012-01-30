package com.mindalliance.channels.playbook.ifm.info

import com.mindalliance.channels.playbook.ref.impl.BeanImpl
import com.mindalliance.channels.playbook.geo.Area
import com.mindalliance.channels.playbook.ifm.Defineable
import com.mindalliance.channels.playbook.ref.Ref

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 21, 2008
 * Time: 3:15:16 PM
 */
class GeoLocation extends BeanImpl implements Defineable {

    AreaInfo areaInfo = new AreaInfo()
    LatLong latLong = new LatLong()  // takes precedence on areaInfo for latlong -- TODO constraint = must be within defined area

    @Override
    List<String> transientProperties() {
        return (List<String>)(super.transientProperties() + ['longitude', 'latitude', 'defined', 'areaType'])
    }

    String toString() {
       if (!isDefined()) return "Unknown"
       String s = ""
       if (areaInfo) s += " ${areaInfo.toString()}"
       s += " [${latLong.toString()}]"
       return s
    }

    boolean isDefined() {
        return areaInfo.isDefined() || latLong.isDefined()
    }

    Ref getAreaType() {
        return areaInfo.areaType
    }

    double getLongitude() {
        if (latLong.isSet()) return latLong.longitude
        double longitude = -1
        if (areaInfo) {
            Area area = areaInfo.getArea()
            if (area.isDefined()) {
               longitude = area.longitude
            }
        }
        return longitude
    }

    double getLatitude() {
        if (latLong.isSet()) return latLong.latitude
        double latitude = -1
        if (areaInfo) {
            Area area = areaInfo.getArea()
            if (area.isDefined()) {
               longitude = area.latitude
            }
        }
        return latitude
    }

    boolean hasLatLong() {
        return this.latitude >= 0 && this.longitude >= 0
    }

    boolean isWithin(GeoLocation other) {
        if (!isDefined() || !other.isDefined()) return false
        return this.areaInfo.isWithin(other.areaInfo)
    }

    boolean isSameAs(GeoLocation other) {
        if (!isDefined() || !other.isDefined()) return false
        return this.areaInfo.isSameAs(other.areaInfo)
    }

    boolean isNearby(GeoLocation other) {
        if (!isDefined() || !other.isDefined()) return false
        return this.areaInfo.isNearby(other.areaInfo)
    }

    GeoLocation broadenedTo(Ref areaType) {
        return new GeoLocation(areaInfo: areaInfo.broadenedTo(areaType))
    }

}