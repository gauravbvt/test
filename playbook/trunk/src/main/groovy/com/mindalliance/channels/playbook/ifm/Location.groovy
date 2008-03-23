package com.mindalliance.channels.playbook.ifm

import com.mindalliance.channels.playbook.geo.Area

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Mar 21, 2008
* Time: 11:45:49 AM
*/
class Location extends IfmElement {

    String street
    String city     // required if street set
    String county
    String state    // required if either county or city set
    String country  // required
    String code
    transient Area area  // force recalculate on change and don't persist

    Area getArea() { // null if Location is unknown
        return area ?: (area = Area.locate(this)) // lazy init
    }

    @Override
    boolean equals(Object obj) {
      if (!obj instanceof Location) return false
      Location loc = (Location)obj
      if (street != loc.street) return false
      if (city != loc.city) return false
      if (county != loc.county) return false
      if (state != loc.state) return false
      if (country != loc.country) return false
      if (code != loc.code) return false
      return true
    }

    @Override
    int hashCode() {
        int hash = 1
        if (street) hash = hash * 31 + street.hashCode()
        if (city) hash = hash * 31 + city.hashCode()
        if (county) hash = hash * 31 + county.hashCode()
        if (state) hash = hash * 31 + state.hashCode()
        if (country) hash = hash * 31 + country.hashCode()
        if (code) hash = hash * 31 + code.hashCode()
        return hash
    }

    String toString() {
        return "<street:$street\ncity:$city\ncounty:$county\nstate:$state\ncountry:$country\ncode:$code>"
    }

    void changed() {
        area = null
    }

    void beforeStore() {
        area = null
    }

}