// Copyright (C) 2006 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.support;

import com.mindalliance.channels.support.GUID;

/**
 * A creator of GUIDs.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision:46 $
 */
public interface GUIDFactory {

    /**
     * Create a new globally unique ID.
     * @return the new ID
     */
    GUID newGuid();
}
