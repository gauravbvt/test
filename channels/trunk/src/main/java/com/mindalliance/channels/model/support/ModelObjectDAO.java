// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.model.support;

import com.mindalliance.channels.model.ModelElement;

/**
 * Persistence interface for model objects.
 *
 * @see ModelElement
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 */
public interface ModelObjectDAO {

    /**
     * Save an object to the persistent store.
     * @param object the object
     */
    void save( ModelElement object );

    /**
     * Delete an object from the persistent store.
     * @param object the object
     */
    void delete( ModelElement object );

    // clone.transfer( object );
    // rollback?
    // commit?
    // optimistic locking

    // ownership managers <--- rules
    // owners/authority --> changes --> change alerts
    // ...
    // authority re: suggestions
    // contributors --> suggests --> suggestion alerts
    // suggestions, suggestion listener, add/remove
    // vote?
}
