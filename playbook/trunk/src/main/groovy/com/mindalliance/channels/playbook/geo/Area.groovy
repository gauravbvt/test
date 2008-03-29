package com.mindalliance.channels.playbook.geo

import com.mindalliance.channels.playbook.ifm.Location
import org.geonames.Toponym

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Mar 22, 2008
* Time: 12:24:25 PM
*/
class Area implements Comparable, Serializable {

    static final Area UNKNOWN = new UnknownArea()

    private Toponym topo
    private List<Area>nearby
    private List<Area> hierarchy

    Area() {}

    Area(Toponym topo) {
        this.topo = topo
    }

    static AmbiguousArea ambiguous(List<Toponym> topos) {
        return new AmbiguousArea(topos)
    }

    boolean isDefined() {
        return !(isUnknown() || isAmbiguous())
    }

    boolean isUnknown() {
        return false
    }

    boolean isAmbiguous() {
        return false
    }

    static Area locate(Location location) {
         if (!location.country) throw new AreaException("Country not named")
         Area area = GeoService.locate(location)    // return null if the location is unknown or ambiguous
         return area
    }

    // Find nearby places of the same area type
    // TODO -- USELESS - find out why it takes a long time to only return self
    List<Area> findNearbyAreas() {
        return nearby ?: (nearby = GeoService.findNearbyAreas(this))
    }

    // Find the place of higher area type that contains this one
    Area findContainingArea() {
        Area area
        List<Area> hier = findHierarchy()
        if (hier) {
            area = hier[0]
        }
        return area
    }

    // Whether this place is within a given location
    boolean isWithinLocation(Location location)  {
        Area area = locate(location)
        List<Area> hier = findHierarchy()
        boolean within =  hier.any {
            it.geonameId && it.geonameId == area.geonameId
        }
        return within
    }

    List<Area> findHierarchy() {
        if (!hierarchy) {
            hierarchy = GeoService.findHierarchy(this)
        }
        return hierarchy
    }

    boolean isGlobe() {
       return topo.name=="Globe" && topo.featureCode == null
    }

    boolean isContinent() {
            return topo.featureCode =="CONT"
    }

    boolean isCountry() {
        return topo.featureCode == 'PCLI'
    }

    boolean isStateLike() {
       return topo.featureCode == 'ADM1'
    }

    boolean isCountyLike() {
      return topo.featureCode == 'ADM2'
    }

    boolean isCityLike() {
      return GeoService.CITY.contains(topo.featureCode) 
    }

    // forward all the gets to toponym    
    def get(String prop)  {
        return topo."$prop"
    }

    int compareTo(Object other) {
        if (!isDefined()) throw new Exception("Can conly compare a defined area")
        if (!other instanceof Area) throw new IllegalArgumentException("Can't compare an Area to $other")
        if (other.isUnknown()) throw new IllegalArgumentException("Can't compare to an unknow area")
        if (other.isAmbiguous()) throw new IllegalArgumentException("Can't compare to an ambiguous area")
        if (typeCode() > other.typeCode()) return 1
        if (typeCode() < other.typeCode()) return -1
        return topo.name.compareTo(other.topo.name)
    }

    int typeCode() {
        if (isGlobe()) return 5
        if (isContinent()) return 4
        if (isCountry()) return 3
        if (isStateLike()) return 2
        if (isCountyLike()) return 1
        if (isCityLike()) return 0
        return -1
    }

    List<String> areaTypeNames() {
        return ['Globe', 'Continent', 'Country', 'State', 'County', 'City']
    }

    String featureCodeFromTypeName(String typeName) {
        switch(typeName) {
            case 'Globe': return null; break
            case 'Continent': return 'CONT'; break
            case 'Country': return 'PCLI'; break
            case 'State': return 'ADM1'; break
            case 'County': return 'ADM2'; break
            case 'City': return 'PPL'; break
            default: throw new IllegalArgumentException("Unknow area type name $typeName")
        }
    }
}