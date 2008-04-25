package com.mindalliance.channels.playbook.ifm.environment

import com.mindalliance.channels.playbook.ifm.IfmElement
import com.mindalliance.channels.playbook.ifm.info.LocationInfo
import com.mindalliance.channels.playbook.ref.Ref

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 21, 2008
 * Time: 3:30:47 PM
 */
class Place extends IfmElement {

    String name = ''
    Ref placeType
    LocationInfo locationInfo = new LocationInfo() // -- required
    
}