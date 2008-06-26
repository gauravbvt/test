package com.mindalliance.channels.playbook.ifm.spec

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.Defineable
import com.mindalliance.channels.playbook.ref.Bean
import org.joda.time.Duration

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 25, 2008
 * Time: 12:38:03 PM
 */
interface Spec extends Defineable {

    // Context-independent matching
    boolean matches(Bean bean)
    // Context-dependent matching
    boolean matchesAsOf(Ref element, Ref informationAct)  // first set to profile
    boolean narrows(Spec spec)

}