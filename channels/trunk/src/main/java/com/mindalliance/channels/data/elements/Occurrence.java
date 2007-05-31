// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.elements;

import com.beanview.annotation.PropertyOptions;
import com.mindalliance.channels.data.components.Caused;
import com.mindalliance.channels.data.reference.Located;
import com.mindalliance.channels.data.support.Duration;

/**
 * Something in a scenario that happens in time, in space and possibly
 * with a known cause.
 *
 * @author jf
 * @version $Revision$
 * @param <T> the type of the causes
 */
public interface Occurrence<T extends Occurrence>
    extends ScenarioElement, Located, Caused<T> {

    /**
     * An incident is an occurrence with no stated cause.
     */
    @PropertyOptions(ignore=true, editable=false)
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
