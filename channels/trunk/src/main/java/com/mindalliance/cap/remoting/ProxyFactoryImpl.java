// Copyright (C) 2006 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.cap.remoting;

import java.lang.reflect.Proxy;

/**
 * A creator of proxies to remote objects.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 */
public class ProxyFactoryImpl {

    private Exporter exporter ;

    //---------------------------------------------
    public ProxyFactoryImpl() {
    }

    //---------------------------------------------
    public RemoteJavaBean createProxy(
            AbstractRemotableBean local, Class remoteInterface ) {

        return (RemoteJavaBean) Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class[] { remoteInterface, RemoteJavaBean.class },
                new RemoteJavaBeanImpl(
                        getExporter().getClient(), local.getGUID(),
                        remoteInterface )
        );
    }

    //---------------------------------------------
    public final Exporter getExporter() {
        return this.exporter;
    }

    public void setExporter( Exporter transport ) {
        this.exporter = transport;
    }
}
