package com.mindalliance.channels.export;

import com.mindalliance.channels.Exporter;
import com.mindalliance.channels.Importer;
import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.dao.Journal;
import com.mindalliance.channels.model.Connector;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.model.Scenario;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Bogus, no-persistence import/exporter.
 */
public class DummyExporter implements ImportExportFactory, Importer, Exporter {

    public DummyExporter() {
    }

    /**
     * Create an import context.
     * @param service the query service
     * @param plan the current plan
     * @return an importer
     */
    public Importer createImporter( QueryService service, Plan plan ) {
        return this;
    }

    /**
     * Create an export context.
     * @param service the query service
     * @param plan the current plan
     * @return an exporter
     */
    public Exporter createExporter( QueryService service, Plan plan ) {
        return this;
    }

    /**
     * Export a journal on the given stream.
     * @param journal a journal
     * @param stream the stream
     * @throws IOException on errors
     */
    public void export( Journal journal, OutputStream stream ) throws IOException {
    }

    /**
     * Export all data on the given stream.
     * @param plan the plan to export
     * @param stream the stream
     * @throws IOException on errors
     */
    public void export( Plan plan, OutputStream stream ) throws IOException {
    }

    /**
     * Export a scenario on the given stream.
     * @param scenario the scenario
     * @param stream the stream
     * @throws IOException on errors
     */
    public void export( Scenario scenario, OutputStream stream ) throws IOException {
    }

    /**
     * The mime type of files from which scenarios are imported.
     *
     * @return -- a mime type
     */
    public String getMimeType() {
        return "text/plain";
    }

    /**
     * Current version
     *
     * @return -- a string
     */
    public String getVersion() {
        return "";
    }

    /**
     * Import a journal from a stream.
     *
     * @param stream an input stream
     * @return a journal
     * @throws IOException on errors
     */
    public Journal importJournal( FileInputStream stream ) throws IOException {
        return new Journal();
    }

    /**
     * Import all persisted data from a stream.
     *
     * @param stream an input stream @throws IOException on errors
     * @throws IOException on errors
     */
    public void importPlan( FileInputStream stream ) throws IOException {
    }

    /**
     * Import a scenario from a stream.
     *
     * @param stream an input stream
     * @return a map with scenario and proxy connectors
     * @throws IOException on errors
     */
    public Scenario importScenario( InputStream stream ) throws IOException {
        return new Scenario();
    }

    /**
     * Load scenario, do not reconnect external flows.
     *
     *
     * @param inputStream an input stream
     * @return mapped results
     * @throws IOException on errors
     */
    public Map<String, Object> loadScenario( InputStream inputStream ) throws IOException {
        return new HashMap<String,Object>();
    }

    /**
     * Reconnect external flows given proxy connectors and idMap
     *
     * @param proxyConnectors a map of proxy connectors
     * (external connectors stand-ins) with specs of external connectors
     * @param loadingPlan is a plan being loaded
     */
    public void reconnectExternalFlows(
            Map<Connector, List<ConnectionSpecification>> proxyConnectors, boolean loadingPlan ) {
    }

    /**
     * Reload a scenario from xml.
     *
     * @param xml an xml string
     * @return a map with scenario and proxy connectors
     */
    public Scenario restoreScenario( String xml ) {
        return new Scenario();
    }
}
