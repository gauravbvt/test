// Copyright (C) 2006 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.remoting;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * A serializable client that can communicate with a service exported via an
 * exporter.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 */
public interface Importer {

    /**
     * Invoke a method on a remote object.
     *
     * @param method the method to invoke
     * @param args the arguments to the method (or null)
     * @return the result of the method, wrapped if primitive, null for void
     * return types.
     * @throws InvocationTargetException for error that occurred on the
     * server
     * @throws IOException on communication error with the server
     */
    Object invoke( Method method, Object[] args )
        throws InvocationTargetException, IOException ;

    /**
     * Add a remote listener to this service.
     *
     * @param guid the GUID of the object to listen to
     * @param client the client to notify on events
     */
    void addListener( GUID guid, Importer client );

    /**
     * Remove a remote listener to this service.
     *
     * @param guid the GUID of the object to listen to
     * @param client the client to remove
     */
    void removeListener( GUID guid, Importer client );

}
