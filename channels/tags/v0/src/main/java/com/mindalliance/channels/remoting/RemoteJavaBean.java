// Copyright (C) 2006 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.remoting;

/**
 * A client proxy on a JavaBean residing on another server.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 */
public interface RemoteJavaBean extends JavaBean {

    /**
     * Make a shallow copy of this remote object.
     * @return a copy of the remote object, with proxies to JavaBean
     * properties.
     */
    CopiedJavaBean takeCopy();

    /**
     * Return the internal copy object.
     * @return null, if takeCopy() was not called previously
     */
    CopiedJavaBean getCopy();

    /**
     * Indicate that a copy of the remote object has been taken.
     * @return true if getCopy() will not return null
     */
    boolean isCopied();

}
