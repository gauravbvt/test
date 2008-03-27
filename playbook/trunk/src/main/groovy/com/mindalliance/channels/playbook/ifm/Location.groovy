package com.mindalliance.channels.playbook.ifm

import com.mindalliance.channels.playbook.geo.Area
import com.mindalliance.channels.playbook.geo.UnknownAreaException
import com.mindalliance.channels.playbook.geo.AmbiguousAreaException
import com.mindalliance.channels.playbook.geo.ServiceFailureAreaException
import com.mindalliance.channels.playbook.ref.impl.BeanImpl
import com.mindalliance.channels.playbook.geo.AreaException

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Mar 21, 2008
* Time: 11:45:49 AM
*/
class Location extends BeanImpl implements Comparable {

    String street = ''
    String city = '' // required if street set
    String county = ''
    String state = '' // required if either county or city set
    String country = '' // required
    String code = ''
    Area area // force recalculate on change and don't persist

    Area getArea() {
        return getArea(true)
    }
    
    Area getArea(boolean remember) {
        Area a = area
        if (!a) {
            a = findArea()
            if (remember) area = a
        }
        return a
    }

    boolean isAreaDefined() {
        return getArea().isDefined()
    }

    Area findArea() {// null if Location is unknown
        Area a
        try {
            a = Area.locate(this)
        }
        catch (UnknownAreaException e) {
            a = Area.UNKNOWN
        }
        catch (AmbiguousAreaException e) {
            a = Area.ambiguous(e.topos)
        }
        catch (ServiceFailureAreaException e) {
            System.err.println("Geo service failure $e")
            a = Area.UNKNOWN
        }
        catch (AreaException e) {
            a = Area.UNKNOWN
        }
        return a
    }

    @Override
    List<String> transientProperties() {
        return super.transientProperties() + ['area']
    }

    @Override
    boolean equals(Object obj) {
        if (!obj instanceof Location) return false
        Location loc = (Location) obj
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

    @Override
    void detach() {
        area = null
    }

    public int compareTo(Object other) {
        if (!other instanceof Location) throw new IllegalArgumentException("Can't compare a location to $other")
        return this.getArea().compareTo(other.getArea())
    }

}