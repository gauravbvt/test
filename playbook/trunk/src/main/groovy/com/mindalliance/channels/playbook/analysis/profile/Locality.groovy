package com.mindalliance.channels.playbook.analysis.profile

import com.mindalliance.channels.playbook.ifm.info.Location
import com.mindalliance.channels.playbook.ifm.playbook.InformationAct
import com.mindalliance.channels.playbook.ref.Ref

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 7, 2008
 * Time: 8:04:57 PM
 */
class Locality extends ProfileElement {

    Location location


    Locality(InformationAct act, Ref agent, Location location) {
        super(act, agent)
        this.location = location
    }

}