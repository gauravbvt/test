package com.mindalliance.channels.playbook.ifm

import com.mindalliance.channels.playbook.ifm.info.Location
import com.mindalliance.channels.playbook.ref.Bean

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Apr 17, 2008
* Time: 11:09:00 AM
*/
interface Locatable extends Bean {

    Location getLocation()

}