package com.mindalliance.channels.playbook.ifm.info

import com.mindalliance.channels.playbook.ifm.environment.Place
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.project.Project

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Apr 17, 2008
* Time: 9:54:38 AM
*/
class PlaceLocation extends Location {   // any kind of real or abstract location that is not geographical

    List<String> directions = [] // names of outer locations, in order of containment  e.g. building X, floor Y, room Z

    Place place // identified by name and directions from some "places service"

    @Override
    List<String> transientProperties() {
        return super.transientProperties() + ['place', 'name', 'geoLocation']
    }

    String getName() {
        String name = ""
        directions.each {  direction ->
            name += " $direction"
        }
        return name.trim()
    }

    Ref getPlace() {
        place = Project.currentProject().findPlace(directions)
    }

    @Override
    void detach() {
        place = null
    }

    public GeoLocation findGeoLocation() {
        return this.place?.geoLocation
    }
}