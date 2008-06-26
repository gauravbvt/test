package com.mindalliance.channels.playbook.ifm.project.environment

import com.mindalliance.channels.playbook.ifm.info.GeoLocation
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.Described
import com.mindalliance.channels.playbook.ifm.project.ProjectElement
import com.mindalliance.channels.playbook.ifm.Named
import com.mindalliance.channels.playbook.ifm.project.Project

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 21, 2008
 * Time: 3:30:47 PM
 */
class Place extends ProjectElement implements Named, Described {

    String name = ''
    String description = ''
    Ref placeType
    Ref enclosingPlace
    GeoLocation geoLocation = new GeoLocation()   // Left undefined unless the place itself is geolocated

    GeoLocation findGeoLocation() { // defaults to undefined geolocation
        if (geoLocation.isDefined()) {
            return geoLocation
        }
        else {
            if (enclosingPlace) {
                GeoLocation geoLoc = enclosingPlace.findGeoLocation() // TODO deal with risk of cycle and infinite loop
                if (geoLocation) {
                    return geoLoc
                }
                else {
                    return new GeoLocation() // return undefined geolocation
                }
            }
            else {
                return new GeoLocation()
            }
        }
    }

    boolean isWithin(Ref place) {
        if (!enclosingPlace) return false
        if (place == enclosingPlace) return true
        return enclosingPlace.isWithin(place)
    }

    boolean isNearby(Ref place) { // both places share common enclosing place... TODO -- improve on this
        if (!enclosingPlace || !place.enclosingPlace) return false
        if (place.enclosingPlace == enclosingPlace) return true
        return false
    }

    Ref broadenedTo(Ref otherPlaceType) {
        if (placeType == otherPlaceType ) return this.reference
        if (enclosingPlace) {
            return enclosingPlace.broadenedTo(otherPlaceType)
        }
        else {
            return null
        }
    }

    // QUERIES

    List<Ref> findAllCandidateEnclosingPlaces() {
        if (placeType) {
            return project.places.findAll{place ->
                place != this && place.placeType && placeType.implies(place.placeType)
            }
        }
        else {
            return []     // return empty list if this place is not classified
        }
    }

    // END QUERIES
    
}