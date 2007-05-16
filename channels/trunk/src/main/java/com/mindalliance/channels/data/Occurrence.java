/*
 * Created on Apr 25, 2007
 */
package com.mindalliance.channels.data;

import com.mindalliance.channels.data.support.Duration;

/**
 * Something in a scenario that happens in time, in space and possibly
 * with a known cause.
 * 
 * @author jf
 */
public interface Occurrence extends ScenarioElement, Located, Caused {

    /**
     * An incident is an occurrence with no stated cause.
     * 
     * @return
     */
    boolean isIncident();

    Duration getEnd();

}
