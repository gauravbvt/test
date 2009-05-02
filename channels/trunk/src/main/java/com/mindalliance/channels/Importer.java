package com.mindalliance.channels;

import com.mindalliance.channels.dao.Journal;
import com.mindalliance.channels.export.ConnectionSpecification;
import com.mindalliance.channels.model.Connector;
import com.mindalliance.channels.model.Scenario;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Scenario import mechanism.
 */
public interface Importer extends Service {

    /**
     * Import a scenario from a stream.
     *
     * @param stream an input stream
     * @return a map with scenario, idMap and proxy connectors
     * @throws IOException on errors
     */
    Scenario importScenario( InputStream stream ) throws IOException;

    /**
     * The mime type of files from which scenarios are imported.
     *
     * @return -- a mime type
     */
    String getMimeType();

    /**
     * Current version
     *
     * @return -- a string
     */
    String getVersion();

    /**
     * Import all persisted data from a stream.
     *
     * @param stream an input stream
     * @return an id translation map
     * @throws java.io.IOException on errors
     */
    Map<Long, Long> importAll( FileInputStream stream ) throws IOException;

    /**
     * Import a journal from a stream.
     *
     * @param stream an input stream
     * @return a journal
     * @throws java.io.IOException on errors
     */
    Journal importJournal( FileInputStream stream ) throws IOException;

    /**
     * Load scenario, do not reconnect external flows.
     *
     * @param inputStream an input stream
     * @return mapped results
     * @throws java.io.IOException on errors
     */
    Map<String, Object> loadScenario( InputStream inputStream ) throws IOException;

    /**
     * Reconnect external flows given proxy connectors and idMap
     *
     * @param idMap a map of exported to imported ids
     * @param proxyConnectors a map of proxy connectors
     * (external connectors stand-ins) with specs of external connectors
     */
    void reconnectExternalFlows(
            Map<String, Long> idMap,
            Map<Connector, ConnectionSpecification> proxyConnectors );
}

