package com.mindalliance.channels.playbook.ifm.definition

import com.mindalliance.channels.playbook.ref.Bean
import com.mindalliance.channels.playbook.ifm.playbook.InformationAct
import com.mindalliance.channels.playbook.ifm.Locatable
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.info.Location
import com.mindalliance.channels.playbook.ifm.Agent
import com.mindalliance.channels.playbook.profile.AgentProfile
import com.mindalliance.channels.playbook.ifm.info.GeoLocation

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 19, 2008
 * Time: 8:46:42 PM
 */
class LocationDefinition extends Definition {

    boolean locationIsAPlace = false // whether the defined location is a place
    Ref placeType               // classification -- if set the location must be a place of this type
    boolean negated = false // if affirmed, all tests must succeed. if negated, all tests must fail
    boolean byGeoLocation = false // specified by given geolocation
    boolean withinGeoLocation = true  // else nearby
    GeoLocation geoLocation = new GeoLocation()
    boolean byJurisdiction = false // specified by a jurisdiction
    boolean withinJurisdiction = true  // else nearby
    Ref jurisdictionable
    boolean byProximity = false // by scoped proximity to another location
    boolean withinProximity = true // else nearby
    Ref proximityAreaType   // xor
    Ref proximityPlaceType  // xor
    Ref proximalPlace               // xor
    Ref proximalLocatable           // xor
    Ref proximalJurisdictionable    // xor

    void setProximityAreaType (Ref areaType) {
        proximityAreaType = areaType
        proximityPlaceType = null
    }

    void setProximityPlaceType (Ref placeType) {
        proximityPlaceType = placeType
        proximityAreaType = null
    }

    void setProximalLocatable(Ref locatable) {
        proximalLocatable = locatable
        proximalJurisdictionable = null
        proximalPlace = null
    }

    void setProximalJurisdictionable(Ref jurisdictionable) {
        proximalJurisdictionable = jurisdictionable
        proximalLocatable = null
        proximalPlace = null
    }

    void setProximalPlace(Ref place) {
        proximalPlace = place
        proximalLocatable = null
        proximalJurisdictionable = null
    }

    void setLocationIsAPlace(boolean isAPlace) {
        locationIsAPlace = isAPlace
        if (!isAPlace) placeType = null
    }

    Class<? extends Bean> getMatchingDomainClass() {
        return Location.class
    }

    boolean matchesAll() {
        return !placeType && (!byGeoLocation || !geoLocation.isDefined()) &&
                (!byJurisdiction || !jurisdictionable as boolean) &&
                (!byProximity || (!proximalLocatable as boolean && !proximalJurisdictionable as boolean && !proximalPlace as boolean))
    }

    MatchResult match(Bean bean, InformationAct informationAct) {
        Location location = (Location)bean
        // Can't match a non-place to a place type
        if (locationIsAPlace && !location.isAPlace()) {
            return new MatchResult(matched:false, failures:["$location is not a place"])
        }
        // If a place check if of the specified type, if given
        if (locationIsAPlace && placeType as boolean && location.isAPlace()) {
            if (location.placeType as boolean && location.placeType != placeType) {
                return new MatchResult(matched:false, failure:["$location is not a place of the specified type $placeType"])
            }
        }
        GeoLocation locationGeoLocation = location.effectiveGeoLocation
        // by geolocation
        if (byGeoLocation) {
            boolean matched = geoLocationsMatch(locationGeoLocation, withinGeoLocation, geoLocation)
            if (negated && matched) {
                return new MatchResult(matched:false, failure:["$location must not be ${relationString(withinGeoLocation)} $geoLocation"])
            }
           if (!negated && !matched) {
               return new MatchResult(matched:false, failure:["$location must be ${relationString(withinGeoLocation)} $geoLocation"])
            }
        }
        // by jurisdiction
        if (byJurisdiction && jurisdictionable as boolean) {
           GeoLocation jusrisdictionGeoLocation = jurisdictionable.jurisdiction.effectiveGeoLocation
            boolean matched = geoLocationsMatch(jusrisdictionGeoLocation, withinJurisdiction, geoLocation)
            if (negated && matched) {
                return new MatchResult(matched:false, failure:["$location must not be ${relationString(withinGeoLocation)} the jurisdiction of $jurisdictionable"])
            }
           if (!negated && !matched) {
               return new MatchResult(matched:false, failure:["$location must be ${relationString(withinGeoLocation)} the jurisdiction of $jurisdictionable"])
            }
        }
        // by relative proximity
        if (byProximity) {
            // If related to a place...
            Ref targetPlace = findProximalPlace(informationAct)
            if (targetPlace as boolean) {
                // location's place is within|nearby target place
                if (location.isAPlace()) {
                    if (withinProximity) {
                        boolean within = location.place.isWithin(targetPlace)
                        if (negated && within) {
                            return new MatchResult(matched:false, failure:["$location must not be in $targetPlace"])
                        }
                        if (!negated && !within) {
                            return new MatchResult(matched:false, failure:["$location must be in $targetPlace"])
                        }
                        else { // nearby
                            boolean nearby = location.place.isNearby(targetPlace)
                            if (negated && nearby) {
                                return new MatchResult(matched:false, failure:["$location must not be nearby $targetPlace"])
                            }
                            if (!negated && !nearby) {
                                return new MatchResult(matched:false, failure:["$location must be nearby $targetPlace"])
                            }
                        }
                    }
                }
                // location's geolocation within|nearby target place's geolocation
                else {
                    GeoLocation targetPlaceGeoLocation = targetPlace.findGeoLocation()
                    boolean matched = geoLocationsMatch(locationGeoLocation, withinProximity, targetPlaceGeoLocation)
                    if (negated && matched) {
                        return new MatchResult(matched:false, failure:["$location must not be ${relationString(withinProximity)} ${relatedString()}"])
                    }
                    if (!negated && !matched) {
                        return new MatchResult(matched:false, failure:["$location must be ${relationString(withinProximity)} ${relatedString()}"])
                    }
                }
            }
            else { // location's geolocation within|nearby target geoLocation
                GeoLocation proximalGeoLocation = findProximalGeoLocation(informationAct)
                boolean matched = geoLocationsMatch(locationGeoLocation, withinProximity, proximalGeoLocation)
                if (negated && matched) {
                    return new MatchResult(matched:false, failure:["$location must not be ${relationString(withinProximity)} ${relatedString()}"])
                }
                if (!negated && !matched) {
                    return new MatchResult(matched:false, failure:["$location must be ${relationString(withinProximity)} ${relatedString()}"])
                }
            }
        }
        return new MatchResult(matched:true)
    }

    private boolean geoLocationsMatch(GeoLocation geoLocation, boolean isWithin, GeoLocation otherGeoLocation) {
       if (isWithin) {
            return geoLocation.isWithin(otherGeoLocation)
       }
        else {
           return geoLocation.isNearby(otherGeoLocation)
       }
    }

    private String relationString(boolean isWithin) {
        return isWithin ? "within" : "nearby"
    }

    private String relatedString() {
        if (proximalPlace as boolean) return "place ${proximalPlace.deref()}"
        if (proximalLocatable as boolean) return "the location of ${proximalLocatable.deref()}"
        if (proximalJurisdictionable as boolean) return "the location of ${proximalJurisdictionable.deref()}"
        return "?"
    }

    private GeoLocation findProximalGeoLocation(InformationAct informationAct) {
        GeoLocation proximalGeoLocation = null
        if (proximalPlace as boolean) {
            proximalGeoLocation = proximalPlace.findGeoLocation()
        }
        else if (proximalLocatable as boolean) {
            Location proximalLocation = currentLocationOf((Locatable)proximalLocatable.deref(), informationAct)
            proximalGeoLocation = proximalLocation.effectiveGeoLocation
        }
        else if (proximalJurisdictionable as boolean) {
           proximalGeoLocation = proximalJurisdictionable.jurisdiction.effectiveGeoLocation
        }
        if (proximalGeoLocation && proximityAreaType as boolean) {
            return proximalGeoLocation.broadenedTo(relatedAreaType)
        }
        else {
            return proximalGeoLocation
        }
    }

    private Ref findProximalPlace(InformationAct informationAct) {
        Ref place = null
        if (proximalPlace as boolean) {
            place = proximalPlace
        }
        else if (proximalLocatable as boolean) {
            Location proximalLocation = currentLocationOf((Locatable)proximalLocatable.deref(), informationAct)
            if (proximalLocation.isAPlace()) {
                place = proximalLocation.place
            }
        }
        else if (proximalJurisdictionable as boolean) {
            Location jurisdiction = proximalJurisdictionable.jurisdiction
            if (jurisdiction.isAPlace()) {
                place = jurisdiction.place
            }
        }
        if (place as boolean && proximityPlaceType as boolean) {
            return place.broadenedTo(proximityPlaceType)
        }
        else {
            return place
        }
    }

    private Location currentLocationOf(Locatable locatable, InformationAct informationAct) {
        if (locatable instanceof Agent) {
            AgentProfile profile = AgentProfile.forAgentAt((Agent)locatable, informationAct)
            return profile.location
        }
        else {
            return locatable.location
        }
    }

    MatchResult fullMatch(Bean bean, InformationAct informationAct) {
        return null;  // TODO
    }

    boolean narrows(MatchingDomain matchingDomain) {
        return false;  // TODO
    }

}