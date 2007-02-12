// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.model.support;

import com.mindalliance.channels.model.AbstractModelObject;

/**
 * Persistence interface for model objects.
 *
 * @see AbstractModelObject
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 */
public interface ModelObjectDAO {

    // clone.transfer( object );
    void save( AbstractModelObject object );

    void delete( AbstractModelObject object );

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
