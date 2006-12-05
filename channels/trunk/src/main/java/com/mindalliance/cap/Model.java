// Copyright (C) 2006 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.cap;

import com.mindalliance.cap.model.ModelException;

/**
 * The CAP model interface.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 */
public interface Model {

    Object get( Object path ) throws ModelException;
    void set( Object path, Object value ) throws ModelException;

}
