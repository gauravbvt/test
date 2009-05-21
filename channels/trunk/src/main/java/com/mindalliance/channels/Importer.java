package com.mindalliance.channels;

import com.mindalliance.channels.dao.Journal;
import com.mindalliance.channels.export.ConnectionSpecification;
import com.mindalliance.channels.model.Connector;
import com.mindalliance.channels.model.Scenario;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Scenario import mechanism.
 */
public interface Importer extends Service {

    /**
     * Import a scenario from a stream.
     *
     * @param stream an input stream
     * @return a map with scenario and proxy connectors
     * @throws IOException on errors
     */
    Scenario importScenario( InputStream stream ) throws IOException;

    /**
     * Reload a scenario from xml.
     *
     * @param xml an xml string
     * @return a map with scenario and proxy connectors
     */
    Scenario restoreScenario( String xml );
    
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
     * @throws java.io.IOException on errors
     */
    void importAll( FileInputStream stream ) throws IOException;

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
     * @param proxyConnectors a map of proxy connectors
     * (external connectors stand-ins) with specs of external connectors
     * @param loadingPlan is a plan being loaded
     */
    void reconnectExternalFlows(
            Map<Connector, List<ConnectionSpecification>> proxyConnectors,
            boolean loadingPlan);
}

