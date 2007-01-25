// Copyright (C) 2006 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.model;

import com.mindalliance.channels.GUID;
import com.mindalliance.channels.impl.AbstractJavaBean;

/**
 * An object in the model.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 */
public abstract class AbstractModelObject extends AbstractJavaBean {

    private GUID guid;

    /**
     * Create a new model object.
     * This should only be called by the factory.
     * @see ModelObjectFactory
     *
     * @param guid the unique guid
     */
    AbstractModelObject( GUID guid ) {
        super();
        this.guid = guid ;
    }

    /**
     * Return the guid of this object.
     */
    public final GUID getGuid() {
        return this.guid;
    }
}
