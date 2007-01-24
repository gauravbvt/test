// Copyright (C) 2006 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels;

import java.util.Collection;

/**
 * A collection of external agencies that are collaborated with.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 */
public interface Agencies {

    /**
     * Get the current collaborators in the list.
     * @return currently registered collaborators
     */
    Collection<Agency> getAgencies();

    /**
     * Add a listener for changes to agencies subscriptions.
     * @param listening the new listener
     */
    void addAgenciesListener( AgenciesListener listening );

    /**
     * Remove a listener for changes to agencies subscriptions.
     * @param listening the listener to remove
     */
    void removeAgenciesListener( AgenciesListener listening );
}
