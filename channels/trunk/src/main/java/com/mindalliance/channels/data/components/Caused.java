// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.components;

import com.mindalliance.channels.data.elements.Timed;

/**
 * Caused by something.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision:$
 */
public interface Caused extends Timed {

    /**
     * Get the cause.
     */
    Cause getCause();

}
