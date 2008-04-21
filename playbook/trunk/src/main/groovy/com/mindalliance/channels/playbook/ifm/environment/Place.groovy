package com.mindalliance.channels.playbook.ifm.environment

import com.mindalliance.channels.playbook.ifm.IfmElement
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.info.GeoLocation

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Apr 17, 2008
* Time: 10:35:49 AM
*/
class Place extends IfmElement {

    String name = ''
    String description = ''
    Ref locationType
    Ref outerPlace
    GeoLocation geoLocation // where the place is located, if applicable

    GeoLocation findGeoLocation() {
        if (geoLocation){
            return geoLocation
        }
        else if (outerPlace) {
            return outerPlace.findGeoLocation()
        }
        else {
            return null
        }
    }

}