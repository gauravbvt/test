package com.mindalliance.channels.dao;

import com.mindalliance.channels.export.ConnectionSpecification;
import com.mindalliance.channels.model.Connector;
import com.mindalliance.channels.model.Segment;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Segment import mechanism.
 */
public interface Importer {

    /**
     * Import a segment from a stream.
     *
     * @param stream an input stream
     * @return a map with segment and proxy connectors
     * @throws IOException on errors
     */
    Segment importSegment( InputStream stream ) throws IOException;

    /**
     * Reload a segment from xml.
     *
     * @param xml an xml string
     * @return a map with segment and proxy connectors
     */
    Segment restoreSegment( String xml );

    /**
     * The mime type of files from which segments are imported.
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
     * @param stream an input stream @throws IOException on errors
     * @throws IOException on errors
     */
    void importPlan( FileInputStream stream ) throws IOException;

    /**
     * Import a journal from a stream.
     *
     * @param stream an input stream
     * @return a journal
     * @throws IOException on errors
     */
    Journal importJournal( FileInputStream stream ) throws IOException;

    /**
     * Load segment, do not reconnect external flows.
     *
     * @param inputStream an input stream
     * @return mapped results
     * @throws IOException on errors
     */
    Map<String, Object> loadSegment( InputStream inputStream ) throws IOException;

    /**
     * Reconnect external flows given proxy connectors and idMap
     *
     * @param proxyConnectors a map of proxy connectors
     * (external connectors stand-ins) with specs of external connectors
     * @param loadingPlan is a plan being loaded
     */
    void reconnectExternalFlows(
            Map<Connector, List<ConnectionSpecification>> proxyConnectors,
            boolean loadingPlan );
}

