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
class LocationSpec extends BeanImpl implements Spec {

    static final List<String> relations = ['contained', 'encompassing', 'adjoining']

    // String relativeTo // one of {location, jurisdiction} of context resource -- required
    Ref areaType // what kind of area -- defaults to location's area type of relative-to if set, else required
    List<Ref> placeTypes = [] // what kinds of places
    String relation = 'contained'// one of relations

    @Override
    List<String> transientProperties() {
        return (List<String>)(super.transientProperties() + ['relations', 'defined'])
    }
    
    boolean isDefined() {
        return areaType != null
    }


    public boolean matches(Ref element) {
        return false;  //Todo
    }

    public boolean narrows(Spec spec) {
        return false;  //Todo
    }
}