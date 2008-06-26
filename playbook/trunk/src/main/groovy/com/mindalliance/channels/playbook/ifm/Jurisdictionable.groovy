package com.mindalliance.channels.playbook.ifm

import com.mindalliance.channels.playbook.ref.Bean
import com.mindalliance.channels.playbook.ifm.info.Location
import com.mindalliance.channels.playbook.ref.Ref

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 20, 2008
 * Time: 2:13:36 PM
 */
interface Jurisdictionable extends Bean {

    Location getJurisdiction()

}