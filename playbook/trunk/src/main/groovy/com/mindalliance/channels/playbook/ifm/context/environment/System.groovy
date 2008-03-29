package com.mindalliance.channels.playbook.ifm.context.environment

import com.mindalliance.channels.playbook.ref.Ref

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Mar 21, 2008
* Time: 12:11:57 PM
*/
class System extends Resource {

    Ref organization
    Ref admin        // a Position
    List<Ref> roles = []
    String access = ''  // how to access the system
}