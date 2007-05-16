/*
 * Created on May 2, 2007
 */
package com.mindalliance.channels.data;

import com.mindalliance.channels.data.components.Information;

/**
 * Something about which information can be communicated.
 * 
 * @author jf
 */
public interface Describable {

    /**
     * Get information that can be known about something
     * 
     * @return
     */
    Information getDescriptor();

}
