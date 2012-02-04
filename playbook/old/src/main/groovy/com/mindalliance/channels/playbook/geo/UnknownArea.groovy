package com.mindalliance.channels.playbook.geo

import com.mindalliance.channels.playbook.ifm.info.AreaInfo

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Mar 23, 2008
* Time: 12:43:14 PM
*/
class UnknownArea extends Area{

    boolean isUnknown() {
        return true
    }

        List<Area> findNearbyAreas() {
        throw new Exception("Area is unknown")
    }

    Area findContainingArea() {
        throw new Exception("Area is unknown")
    }

    boolean isWithin(Area area) {
        throw new Exception("Area is unknown")
    }

    List<Area> findHierarchy() {
        throw new Exception("Area is unknown")
    }

    boolean isGlobe() {
        return false
    }

    boolean isContinent() {
        return false
    }

    boolean isCountry() {
        return false
    }

    boolean isStateLike() {
        return false
    }

    boolean isCountyLike() {
        return false
    }

    boolean isCityLike() {
        return false
    }

}