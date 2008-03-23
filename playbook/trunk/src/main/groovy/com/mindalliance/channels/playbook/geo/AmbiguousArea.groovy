package com.mindalliance.channels.playbook.geo

import org.geonames.Toponym

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Mar 23, 2008
* Time: 12:43:30 PM
*/
class AmbiguousArea extends Area {

    List<Toponym> topos

    AmbiguousArea(List<Toponym> topos)  {
        this.topos = topos
    }

    // TODO throw exceptions on all methods

}