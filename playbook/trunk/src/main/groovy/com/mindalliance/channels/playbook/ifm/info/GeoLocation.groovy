package com.mindalliance.channels.playbook.ifm.info

import com.mindalliance.channels.playbook.geo.Area
import com.mindalliance.channels.playbook.geo.UnknownAreaException
import com.mindalliance.channels.playbook.geo.AmbiguousAreaException
import com.mindalliance.channels.playbook.geo.ServiceFailureAreaException
import com.mindalliance.channels.playbook.geo.AreaException
import org.apache.log4j.Logger

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Mar 21, 2008
* Time: 11:45:49 AM
*/
class GeoLocation extends Location implements Comparable {

    String street = ''
    String city = '' // required if street set
    String county = ''
    String state = '' // required if either county or city set
    String country = '' // required
    String code = ''
    Area area // force recalculate on change and don't persist

    @Override
    boolean isGeoLocation() {
        return true
    }


    @Override
    List<String> transientProperties() {
        return super.transientProperties() + ['area', 'name', 'areaDefined']
    }

    String getName() {
        return toString()
    }

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
            Logger.getLogger(this.class.name).warn("Geo service failure", e)
            a = Area.UNKNOWN
        }
        catch (AreaException e) {
            a = Area.UNKNOWN
        }
        return a
    }

    @Override
    boolean equals(Object obj) {
        if (!obj instanceof GeoLocation) return false
        GeoLocation loc = (GeoLocation) obj
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
        String s = street ? (street + ', ') : ''
        String ci = city ? (city + ', ') : ''
        String ct = county ? (county + ', ') : ''
        String sa = state ? (state + ', ') : ''
        String cn = country ? (country + ', ') : ''
        String cd = code ? code : ''
        String string = "$s$ci$ct$sa$cn$cd"
        if (string && string[string.size()-1] == ',') {
            string = "${string[0..string.size()-2]}"
        }
        return string
    }

    @Override
    void detach() {
        area = null
    }

    public int compareTo(Object other) {
        if (!other instanceof GeoLocation) throw new IllegalArgumentException("Can't compare a location to $other")
        return this.getArea().compareTo(other.getArea())
    }

   private void writeObject(ObjectOutputStream oos) throws IOException {
       area = null
       oos.defaultWriteObject()
    }

    public GeoLocation findGeoLocation() {
        return this
    }
}