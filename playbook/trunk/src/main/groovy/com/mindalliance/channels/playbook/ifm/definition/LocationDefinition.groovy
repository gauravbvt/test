package com.mindalliance.channels.playbook.ifm.definition

import com.mindalliance.channels.playbook.ref.Bean
import com.mindalliance.channels.playbook.ifm.playbook.InformationAct
import com.mindalliance.channels.playbook.ifm.Locatable
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.info.Location
import com.mindalliance.channels.playbook.ifm.Agent
import com.mindalliance.channels.playbook.profile.AgentProfile
import com.mindalliance.channels.playbook.ifm.info.GeoLocation
import com.mindalliance.channels.playbook.ifm.model.AreaType

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 19, 2008
 * Time: 8:46:42 PM
 */
class LocationDefinition extends Definition {

    static List<String> LocationRelations = ["same", "nearby"]
    boolean placeLocation = false // whether the defined location is a place
    Ref placeType               // classification -- if set the location must be a place of this type
    String relation = "same"    // relation to other, relative location
    Ref relatedAreaType = AreaType.country()        // areaType of relative location. If set, relative location is an area
    Ref relatedPlaceType                            // xor placeType of relative location. If set, relative location is a place
    Ref relatedLocatable                                // the location of a place, event, resource, etc.
    Ref relatedJurisdictionable                         // xor a resource's jurisdiction
    Ref relatedPlace                                    // xor a place
    GeoLocation relatedGeoLocation = new GeoLocation()  // xor a geoLocation is defined with relation "a specific"

    @Override
    List<String> transientProperties() {
        return (List<String>)(super.transientProperties() + ['locationRelations'])
    }

    void setRelatedAreaType (Ref areaType) {
        relatedAreaType = areaType
        relatedPlaceType = null
    }

    void setRelatedPlaceType (Ref placeType) {
        relatedPlaceType = placeType
        relatedAreaType = null
    }

    void setRelatedLocatable(Ref locatable) {
        relatedLocatable = locatable
        relatedJurisdictionable = null
        relatedPlace = null
        relatedGeoLocation = new GeoLocation()
    }

    void setRelatedJurisdictionable(Ref jurisdictionable) {
        relatedJurisdictionable = jurisdictionable
        relatedLocatable = null
        relatedPlace = null
        relatedGeoLocation = new GeoLocation()
    }

    void setRelatedPlace(Ref place) {
        relatedPlace = place
        relatedLocatable = null
        relatedJurisdictionable = null
        relatedGeoLocation = new GeoLocation()
    }

    void setRelatedGeoLocation(GeoLocation geoLocation) {
        relatedGeoLocation =geoLocation
        relatedLocatable = null
        relatedJurisdictionable = null
        relatedPlace = null
    }

    Class<? extends Bean> getMatchingDomainClass() {
        return Location.class
    }

    boolean matchesAll() {
        return !placeType && !relatedLocatable && !relatedJurisdictionable && !relatedPlace && !relatedGeoLocation.isDefined()
    }

    MatchResult match(Bean bean, InformationAct informationAct) {
        Location location = (Location)bean
        // Can't match a non-place to a place type
        if ((placeLocation || placeType as boolean) && !location.isAPlace()) {
            return new MatchResult(matched:false, failures:["$location is not a place"])
        }
        // If a place check if of the specified type, if given
        if (placeLocation && placeType as boolean && location.isAPlace()) {
            if (location.placeType as boolean && location.placeType != placeType) {
                return new MatchResult(matched:false, failure:["$location is not a place of the specified type $placeType"])
            }
        }
        // If related to a place...
        Ref place = findRelatedPlace(informationAct)
        if (place as boolean) {
            // place (rel) place
            if (location.isAPlace()) {
                switch (relation) {
                    case "same":
                        if (!location.place.isWithin(place)) {
                            return new MatchResult(matched:false, failure:["Place $location is not in place $place"])
                        }
                        break
                    case "nearby":
                        if (!location.place.isNearby(place)) {
                            return new MatchResult(matched:false, failure:["Place $location is not nearby $place"])
                        }
                }
            }
            // geoLocation (rel) place
            else {
                GeoLocation relatedPlaceGeoLocation = place.findGeoLocation()
                if (!geoLocationsMatch(location.effectiveGeoLocation, relatedPlaceGeoLocation)) {
                    return new MatchResult(matched:false, failure:["$location is not $relation (as) $place"])
                }
            }
        }
        else { // ? (rel) geoLocation
            GeoLocation relatedGeoLocation = findRelatedGeoLocation(informationAct)
            if (!geoLocationsMatch(location.effectiveGeoLocation, relatedGeoLocation)) {
                    return new MatchResult(matched:false, failure:["$location is not $relation (as) $relatedGeoLocation"])
                }
        }
        return new MatchResult(matched:true)
    }

    private boolean geoLocationsMatch(GeoLocation geoLocation, GeoLocation relatedGeoLocation) {
        switch (relation) {
            case "same":
                    return geoLocation.isWithin(relatedGeoLocation)
            case "nearby":
                    return geoLocation.isNearby(relatedGeoLocation)
        }
    }

    private GeoLocation findRelatedGeoLocation(InformationAct informationAct) {
        GeoLocation geoLocation
        if (relatedPlace as boolean) {
            geoLocation = relatedPlace.findGeoLocation()
        }
        else if (relatedLocatable as boolean) {
            Location relatedLocation = currentLocationOf((Locatable)relatedLocatable.deref(), informationAct)
            geoLocation = relatedLocation.effectiveGeoLocation
        }
        else if (relatedJurisdictionable as boolean) {
           geoLocation = relatedJurisdictionable.jurisdiction.effectiveGeoLocation
        }
        else {
            geoLocation = relatedGeoLocation
        }
        if (relatedAreaType as boolean) {
            return geoLocation.broadenedTo(relatedAreaType)
        }
        else {
            return geoLocation
        }
    }

    private Ref findRelatedPlace(InformationAct informationAct) {
        Ref place = null
        if (relatedPlace as boolean) {
            place = relatedPlace
        }
        else if (relatedLocatable as boolean) {
            Location relatedLocation = currentLocationOf((Locatable)relatedLocatable.deref(), informationAct)
            if (relatedLocation.isAPlace()) {
                place = relatedLocatable.location.place
            }
        }
        else if (relatedJurisdictionable as boolean) {
            Location jurisdiction = relatedJurisdictionable.jurisdiction
            if (jurisdiction.isAPlace()) {
                place = jurisdiction.place
            }
        }
        if (place as boolean && relatedPlaceType as boolean) {
            return place.broadenedTo(relatedPlaceType)
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