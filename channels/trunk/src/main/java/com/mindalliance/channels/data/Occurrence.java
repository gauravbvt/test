// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data;

import com.mindalliance.channels.data.support.Duration;

/**
 * Something in a scenario that happens in time, in space and possibly
 * with a known cause.
 *
 * @author jf
 * @version $Revision$
 */
public interface Occurrence extends ScenarioElement, Located, Caused {

    /**
     * An incident is an occurrence with no stated cause.
     */
    boolean isIncident();

    /**
     * Get the time from the start of the scenario.
     */
    Duration getEnd();

    /**
     * Get the duration for this occurrence.
     * @return 0 if an instant event.
     */
    Duration getDuration();
}
