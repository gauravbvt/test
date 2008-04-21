package com.mindalliance.channels.playbook.ifm.environment

import com.mindalliance.channels.playbook.ifm.IfmElement
import com.mindalliance.channels.playbook.ref.Ref
import org.apache.log4j.Logger

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 20, 2008
 * Time: 9:28:12 AM
 */
class Environment extends IfmElement {

    String name
    List<Ref> places = []
    List<Ref> policies = []

    Ref findPlace(List<String> directions) {
        Ref place
        List<Ref> placesFound
        directions.each {name ->
            if (placesFound == null || !placesFound.isEmpty()) {
                List<Ref> namedPlaces = (List<Ref>) places.findAll {p ->
                    p.name.equalsIgnoreCase(name.trim())
                }
                if (placesFound) {
                    namedPlaces = namedPlaces.findAll {p ->
                        p.outerPlace && placesFound.contains(p.outerPlace)
                    }
                }
                placesFound = namedPlaces
            }
        }
        if (!placesFound) {
            Logger.getLogger(this.class).warn("No place found with directions $directions")
        }
        else if (placesFound.size() == 1) {
            place = placesFound[0]
        }
        else if (placesFound.size() > 1) {
            Logger.getLogger(this.class).warn("Ambiguous directions $directions to place")
        }
        return place
    }

}