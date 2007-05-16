/*
 * Created on May 1, 2007
 */
package com.mindalliance.channels.data;

import com.mindalliance.channels.data.components.Cause;

/**
 * Caused by something.
 * 
 * @author jf
 */
public interface Caused extends Timed {

    /**
     * Get cause.
     * 
     * @return
     */
    Cause getCause();

}
