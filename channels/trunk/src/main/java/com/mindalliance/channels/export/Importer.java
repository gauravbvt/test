package com.mindalliance.channels.export;

import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.dao.Journal;

import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.Map;

/**
 * Scenario import mechanism.
 */
public interface Importer {

    /**
     * Import a scenario from a stream.
     *
     * @param stream an input stream
     * @return the imported scenario
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
     * Import a project from a stream.
     *
     * @param stream an input stream
     * @throws java.io.IOException on errors
     * @return an id translation map
     */
    Map<Long,Long> importProject( FileInputStream stream ) throws IOException;

    /**
     * Import a journal from a stream.
     *
     * @param stream an input stream
     * @return a journal
     * @throws java.io.IOException on errors
     */
    Journal importJournal( FileInputStream stream ) throws IOException;
    
}

