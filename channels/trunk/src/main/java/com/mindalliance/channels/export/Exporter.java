package com.mindalliance.channels.export;

import com.mindalliance.channels.Scenario;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;

/**
 * Scenario export mechanism.
 * @see Importer
 */
public interface Exporter extends Serializable {

    /**
     * Export a scenario on the given stream.
     * @param scenario the scenario
     * @param stream the stream
     * @throws IOException on errors
     */
    void exportScenario( Scenario scenario, OutputStream stream ) throws IOException;

    /**
     * The mime type of files to which scenarios are exported.
     * @return -- a mime type
     */
    String getMimeType();

}
