/*
 * Created on Apr 25, 2007
 */
package com.mindalliance.channels.data;

import com.mindalliance.channels.util.GUID;

/**
 * Something with a globally unique id
 * 
 * @author jf
 */
public interface Unique {

    /**
     * Get globally unique id
     * 
     * @return
     */
    GUID getGuid();

}
