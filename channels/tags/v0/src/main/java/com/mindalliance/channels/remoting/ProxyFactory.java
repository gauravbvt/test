// Copyright (C) 2006 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.remoting;

/**
 * A creator of proxies to local objects.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 */
public interface ProxyFactory {

    //---------------------------------------------
    /**
     * Create a proxy on a local object.
     *
     * @param local the local object
     * @param remoteInterface the front-end for the object
     * @param <T> the type of the remote interface
     * @return a proxy that can be serialized and shipped to a client.
     * The proxy implements the remote interface as well as RemoteJavaBean.
     * @see RemoteJavaBean
     */
    <T> T createProxy(
            AbstractRemotableBean local, Class<T> remoteInterface );
}
