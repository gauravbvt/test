package com.mindalliance.channels.playbook.ifm.info

import com.mindalliance.channels.playbook.ref.impl.BeanImpl
import com.mindalliance.channels.playbook.geo.Area

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 21, 2008
 * Time: 3:15:16 PM
 */
class LocationInfo extends BeanImpl {

    AreaInfo areaInfo = new AreaInfo()
    LatLong latLong = new LatLong()  // takes precedence on areaInfo for latlong

    String toString() {
       String s = ""
       if (areaInfo) s += " ${areaInfo.toString()}"
       s += " [${latLong.toString()}]"
       return s
    }

    @Override
    List<String> transientProperties() {
        return super.transientProperties() + ['longitude', 'latitude']
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

    boolean isWithin(LocationInfo other) {
        return this.areaInfo.isWithin(other.areaInfo)
    }

}