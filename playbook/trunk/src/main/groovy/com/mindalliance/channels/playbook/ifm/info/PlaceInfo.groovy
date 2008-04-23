package com.mindalliance.channels.playbook.ifm.info

import com.mindalliance.channels.playbook.ref.impl.BeanImpl

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Apr 17, 2008
* Time: 9:54:38 AM
*/
class PlaceInfo extends BeanImpl {   // any kind of real or abstract location that is not geographical

    List<PlaceItem> placeItems = []   // ordered outer to inner (e.g. building, floor, room)

    @Override
    List<String> transientProperties() {
        return super.transientProperties() + ['name']
    }

    String getName() {
        String name = ""
        placeItems.each {  placeItem ->
            name += " ${placeItem.name}"
        }
        return name.trim()
    }

}