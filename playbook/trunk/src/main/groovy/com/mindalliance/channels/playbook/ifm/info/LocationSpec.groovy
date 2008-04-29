package com.mindalliance.channels.playbook.ifm.info

import com.mindalliance.channels.playbook.ref.impl.BeanImpl
import com.mindalliance.channels.playbook.ref.Ref

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Apr 17, 2008
* Time: 9:21:45 AM
*/
class LocationSpec extends BeanImpl {

    static final List<String> RELATIONS = ['same', 'contained', 'containing', 'sameOrAdjoining']

    // String relativeTo // one of {location, jurisdiction} of context resource -- required
    Ref areaType // what kind of area -- defaults to location's area type of relative-to if set, else required
    Ref placeType // what kind of place -- defaults to location's place type of relative-to if set, else required
    String relation = 'same'// one of {same, contained, containing, sameOrAdjoining} -- defaults to "containing" -- re. jurisdiction, else location


}