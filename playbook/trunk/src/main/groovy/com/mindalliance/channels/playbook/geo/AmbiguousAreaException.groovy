package com.mindalliance.channels.playbook.geo

import org.geonames.Toponym


/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Mar 23, 2008
* Time: 11:37:11 AM
*/
class AmbiguousAreaException extends AreaException {

    List<Toponym> topos

    AmbiguousAreaException(String message, List<Toponym>topos) {
        super(message)
        this.topos = topos
    }
}