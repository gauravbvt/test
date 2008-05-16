package com.mindalliance.channels.playbook.ifm.playbook

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.info.Timing
import org.joda.time.Duration

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 16, 2008
 * Time: 11:51:48 AM
 */
interface Causable {

    Cause getCause()

    boolean hasTransitiveCause(Ref causable)

    Duration startTime()
}