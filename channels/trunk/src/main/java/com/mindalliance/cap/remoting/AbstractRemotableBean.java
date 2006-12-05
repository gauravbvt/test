// Copyright (C) 2006 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.cap.remoting;

/**
 * A java bean that can have remote listeners.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 */
public class AbstractRemotableBean extends AbstractJavaBean {

    private GUID guid ;

    public AbstractRemotableBean( GUID guid ) {
        super();
        this.guid = guid ;
    }

    public AbstractRemotableBean( GUIDFactory factory ) {
        this( factory.newGuid() );
    }

    public final GUID getGUID() {
        return this.guid;
    }

}
