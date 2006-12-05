// Copyright (C) 2006 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.cap.remoting;

/**
 * An underlying mechanism to send a message to another host.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 */
public interface Exporter {

    /**
     * @return a serializable client that can talk back to this exporter
     */
    Importer getClient();

    /**
     * Add a local hook for remote events.
     *
     * @param guid the GUID of the object to listen to
     * @param remoteListener the property/veto change listener
     */
    void addListener( GUID guid, RemoteListener remoteListener );

    /**
     * Remove a hook for remote events.
     *
     * @param guid the GUID of the object to listen to
     * @param remoteListener the property/veto change listener
     */
    void removeListener( GUID guid, RemoteListener remoteListener );
}
