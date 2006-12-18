// Copyright (C) 2006 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.remoting;

import java.lang.reflect.Proxy;

/**
 * A creator of proxies to remote objects.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 */
public class ProxyFactoryImpl implements ProxyFactory {

    private Exporter exporter ;

    //---------------------------------------------
    /**
     * Default constructor.
     */
    public ProxyFactoryImpl() {
    }

    //---------------------------------------------
    /* (non-Javadoc)
     * @see ProxyFactory#createProxy(AbstractRemotableBean, Class)
     */
    @SuppressWarnings( "unchecked" )
    public <T> T createProxy(
            AbstractRemotableBean local, Class<T> remoteInterface ) {

        if ( getExporter() == null )
            throw new IllegalStateException();

        return (T) Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class[] { remoteInterface, RemoteJavaBean.class },
                new RemoteJavaBeanImpl(
                        getExporter().getClient(), local.getGUID(),
                        remoteInterface )
        );
    }

    //---------------------------------------------
    /**
     * Return the exporter used by the factory.
     */
    public final Exporter getExporter() {
        return this.exporter;
    }

    /**
     * Set the exporter for this factory. createProxy() will throw
     * IllegalState exceptions until one is provided.
     * @param exporter the export mechanism to use for publishing objects.
     */
    public void setExporter( Exporter exporter ) {
        this.exporter = exporter;
    }
}
