package com.mindalliance.channels.playbook.ifm.info

import com.mindalliance.channels.playbook.geo.Area
import com.mindalliance.channels.playbook.geo.UnknownAreaException
import com.mindalliance.channels.playbook.geo.AmbiguousAreaException
import com.mindalliance.channels.playbook.geo.ServiceFailureAreaException
import com.mindalliance.channels.playbook.geo.AreaException
import com.mindalliance.channels.playbook.ref.impl.BeanImpl
import com.mindalliance.channels.playbook.ifm.Defineable
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.taxonomy.AreaType
import org.apache.log4j.Logger

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 21, 2008
 * Time: 3:23:23 PM
 */
class AreaInfo extends BeanImpl  implements Comparable, Defineable {

    String street = ''
    String city = '' // required if street set
    String county = ''
    String state = '' // required if either county or city set
    String country = '' // required
    String code = ''
    Area area // force recalculate on change and don't persist

    boolean isDefined() {
        return country && country.trim().size() > 0
    }

    @Override
    List<String> transientProperties() {
        return (List<String>)(super.transientProperties() + ['area', 'name', 'areaDefined', 'defined', 'areaType'])
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

    Ref getAreaType() {
        if (city) return AreaType.city()
        if (county) return AreaType.county()
        if (state) return AreaType.state()
        if (country) return AreaType.country()
        return AreaType.globe()
    }

    boolean isSameAs(AreaInfo other) {
        return this.area.equals(other.area)
    }

    boolean isWithin(AreaInfo other) {
        return this.area.isWithin(other.area)
    }

    boolean isNearby(AreaInfo other) {
        return this.area.isNearby(other.area)
    }

    AreaInfo broadenedTo(AreaType otherAreaType) {
        if (areaType == otherAreaType) {
            return this
        }
        else {
            AreaInfo broaderAreaInfo = (AreaInfo)this.copy()
            switch(otherAreaType) {
                case AreaType.country(): broaderAreaInfo.state = ''
                case AreaType.state(): broaderAreaInfo.county = ''
                case AreaType.county(): broaderAreaInfo.city = ''
                case AreaType.city(): broaderAreaInfo.street = ''
                default: broaderAreaInfo.code = ''
            }
            return broaderAreaInfo
        }
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
        if (!obj instanceof AreaInfo) return false
        AreaInfo loc = (AreaInfo) obj
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

    int compareTo(Object other) {
        if (!other instanceof AreaInfo) throw new IllegalArgumentException("Can't compare an AreaInfo to $other")
        return this.getArea().compareTo(other.getArea())
    }

   private void writeObject(ObjectOutputStream oos) throws IOException {
       area = null
       oos.defaultWriteObject()
    }
}
