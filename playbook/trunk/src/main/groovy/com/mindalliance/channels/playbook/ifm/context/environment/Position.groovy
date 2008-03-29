package com.mindalliance.channels.playbook.ifm.context.environment

import com.mindalliance.channels.playbook.ifm.Location
import com.mindalliance.channels.playbook.ref.Ref

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Mar 21, 2008
* Time: 12:07:37 PM
*/
class Position extends Resource {

    Location jurisdiction = new Location()
    Organization organization // required
    List<Ref> roles = []

    void beforeStore() {
        jurisdiction.detach()
    }

}