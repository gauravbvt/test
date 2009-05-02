package com.mindalliance.channels;

import com.mindalliance.channels.dao.Journal;
import com.mindalliance.channels.model.Scenario;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Scenario export mechanism.
 * @see Importer
 */
public interface Exporter extends Service {

    /**
     * Export a scenario on the given stream.
     * @param scenario the scenario
     * @param stream the stream
     * @throws IOException on errors
     */
    void exportScenario( Scenario scenario, OutputStream stream ) throws IOException;

    /**
     * Export all data on the given stream.
     * @param stream the stream
     * @throws IOException on errors
     */
    void exportAll( OutputStream stream ) throws IOException;

    /**
     * Export a scenario on the given stream.
     * @param journal a journal
     * @param stream the stream
     * @throws IOException on errors
     */
    void exportJournal( Journal journal, OutputStream stream ) throws IOException;

    /**
     * The mime type of files to which scenarios are exported.
     * @return -- a mime type
     */
    String getMimeType();

    /**
     * Current version
     * @return -- a string
     */
    String getVersion();

}
