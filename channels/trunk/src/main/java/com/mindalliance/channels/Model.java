// Copyright (C) 2006 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels;

import com.mindalliance.channels.model.ModelException;

/**
 * The Channels model interface.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 */
public interface Model {

    /**
     * Get an object using an OGNL path.
     * @param path the path
     * @return the object
     * @throws ModelException on errors
     */
    Object get( Object path ) throws ModelException;

    /**
     * Set an object using an OGNL path.
     * @param path the path
     * @param value the new value
     * @throws ModelException on errors
     */
    void set( Object path, Object value ) throws ModelException;

}
