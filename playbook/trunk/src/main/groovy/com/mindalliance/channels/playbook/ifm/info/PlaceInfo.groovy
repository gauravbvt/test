package com.mindalliance.channels.playbook.ifm.info

import com.mindalliance.channels.playbook.ref.impl.BeanImpl
import com.mindalliance.channels.playbook.ifm.Defineable

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Apr 17, 2008
* Time: 9:54:38 AM
*/
class PlaceInfo extends BeanImpl implements Defineable {   // any kind of real or abstract location that is not geographical

    List<PlaceItem> placeItems = []   // ordered outer to inner (e.g. building, floor, room)

    @Override
    List<String> transientProperties() {
        return super.transientProperties() + ['name', 'empty', 'defined']
    }

    boolean isDefined() {
        return !placeItem.isEmpty()
    }

    String toString() {
        return getName()
    }

    boolean isEmpty() {
        return placeItems.isEmpty()
    }

    String getName() {
        String name = ""
        placeItems.each {  placeItem ->
            name += " ${placeItem.placeName}"
        }
        return name.trim()
    }

}