package com.mindalliance.channels.playbook.ifm

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.playbook.Event
import com.mindalliance.channels.playbook.ifm.info.Location
import com.mindalliance.channels.playbook.ref.Referenceable

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 14, 2008
 * Time: 8:11:48 PM
 */
interface Agent extends Referenceable, Named, Described, Locatable {

    List<Responsibility> getResponsibilities()
    List<Ref> getResourcesAt(Ref event)
    boolean isResourceElement()
    boolean isGroup()
    boolean hasJurisdiction()
    boolean hasLocation()
}