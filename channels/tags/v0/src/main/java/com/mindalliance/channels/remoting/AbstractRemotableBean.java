// Copyright (C) 2006 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.remoting;

/**
 * A java bean that can have remote listeners.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 */
public class AbstractRemotableBean extends AbstractJavaBean {

    private GUID guid ;

    /**
     * Create a new bean.
     * @param guid the global unique ID identifying this bean
     */
    public AbstractRemotableBean( GUID guid ) {
        super();
        this.guid = guid ;
    }

    /**
     * Create a new bean. Convenience method for initializers.
     * @param factory a factory to obtain the guid from.
     */
    public AbstractRemotableBean( GUIDFactory factory ) {
        this( factory.newGuid() );
    }

    /**
     * Return the guid of this object.
     */
    public final GUID getGUID() {
        return this.guid;
    }

}
