package com.mindalliance.channels;

import com.mindalliance.channels.dao.Journal;
import com.mindalliance.channels.model.Scenario;
import com.mindalliance.channels.model.Plan;

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
    void export( Scenario scenario, OutputStream stream ) throws IOException;

    /**
     * Export all data on the given stream.
     * @param plan the plan to export
     * @param stream the stream
     * @throws IOException on errors
     */
    void export( Plan plan, OutputStream stream ) throws IOException;

    /**
     * Export a journal on the given stream.
     * @param journal a journal
     * @param stream the stream
     * @throws IOException on errors
     */
    void export( Journal journal, OutputStream stream ) throws IOException;

    /**
     * The mime type of files to which scenarios are exported.
     * @return -- a mime type
     */
    String getMimeType();

    /**
     * Get current version, for upgrading purposes.
     * @return a string
     */
    String getVersion();

}
