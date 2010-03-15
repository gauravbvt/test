// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.
package com.mindalliance.mindpeer.dao.jpa;

import com.mindalliance.mindpeer.dao.FocusDao;
import com.mindalliance.mindpeer.model.Focus;

/**
 * ...
 */
public class FocusDaoImpl extends AbstractDaoImpl<Focus> implements FocusDao {

    /**
     * Create a new FocusDaoImpl instance.
     */
    public FocusDaoImpl() {
        super( Focus.class );
    }
}
