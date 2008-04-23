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

    AreaInfo areaInfo
    LatLong latLong  // takes precedence on areaInfo for latlong

    double getLongitude() {
        if (latLong) return latLong.longitude
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
        if (latLong) return latLong.latitude
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

}