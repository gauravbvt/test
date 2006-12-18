// Copyright (C) 2006 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels;

/**
 * Listeners to change in registration to an agency registry.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 */
public interface AgenciesListener {

    /**
     * Notification sent when a new agency was added to the regsitry.
     * @param agency the new agency
     */
    void addedAgency( Agency agency );

    /**
     * Notification sent when an agency was removed from the registry.
     * @param agency the removed agency
     */
    void removedAgency( Agency agency );
}
