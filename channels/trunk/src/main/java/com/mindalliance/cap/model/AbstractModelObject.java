// Copyright (C) 2006 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.cap.model;

import com.mindalliance.cap.remoting.AbstractRemotableBean;
import com.mindalliance.cap.remoting.GUID;
import com.mindalliance.cap.remoting.GUIDFactory;

/**
 * An object in the model.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 */
public abstract class AbstractModelObject extends AbstractRemotableBean {

    /**
     * Create a new model object.
     * @param guid the unique guid
     */
    public AbstractModelObject( GUID guid ) {
        super( guid );
    }

    /**
     * Convenience constructor for initializers.
     * @param guidFactory a factory to use to obtain a guid
     */
    public AbstractModelObject( GUIDFactory guidFactory ) {
        super( guidFactory );
    }
}
