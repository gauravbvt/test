package com.mindalliance.channels.playbook.ifm.project.resources

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.info.Location

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Apr 17, 2008
* Time: 11:21:56 AM
*/
class Position extends Resource {

    Location jurisdiction
    List<Ref> managesPositions

    void beforeStore() {
        super.beforeStore()
        if (jurisdiction) jurisdiction.detach()
    }
    
}