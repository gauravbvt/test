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
class Area {

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

    boolean isUnknow() {
        return false
    }

    boolean isAmbiguous() {
        return false
    }

    static Area locate(Location location) {
         assert location.country
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
}