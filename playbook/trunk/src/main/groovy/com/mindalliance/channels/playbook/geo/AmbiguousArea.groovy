package com.mindalliance.channels.playbook.geo

import org.geonames.Toponym
import com.mindalliance.channels.playbook.ifm.info.GeoLocation

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Mar 23, 2008
* Time: 12:43:30 PM
*/
class AmbiguousArea extends Area {

    List<Toponym> topos

    AmbiguousArea(List<Toponym> topos) {
        this.topos = topos
    }

    boolean isAmbiguous() {
        return true
    }

    List<Area> findNearbyAreas() {
        throw new Exception("Area is ambiguous")
    }

    Area findContainingArea() {
        throw new Exception("Area is ambiguous")
    }

    boolean isWithinLocation(GeoLocation location) {
        throw new Exception("Area is ambiguous")
    }

    List<Area> findHierarchy() {
        throw new Exception("Area is ambiguous")
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