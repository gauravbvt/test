package com.mindalliance.channels.playbook.ifm.project.environment

import com.mindalliance.channels.playbook.ifm.info.LocationInfo
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.Describable
import com.mindalliance.channels.playbook.ifm.project.ProjectElement

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 21, 2008
 * Time: 3:30:47 PM
 */
class Place extends ProjectElement implements Describable {

    String name = ''
    String description = ''
    List<Ref> placeTypes = []
    LocationInfo locationInfo = new LocationInfo() // -- required
    
}