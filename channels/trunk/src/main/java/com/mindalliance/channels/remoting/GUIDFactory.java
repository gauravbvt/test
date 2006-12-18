// Copyright (C) 2006 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.remoting;

/**
 * A creator of GUIDs.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 */
public interface GUIDFactory {

    /**
     * Create a new globally unique ID.
     * @return the new ID
     */
    GUID newGuid();
}
