package com.mindalliance.channels.export;

import com.mindalliance.channels.Scenario;

import java.io.IOException;
import java.io.InputStream;

/**
 * Scenario import mechanism.
 */
public interface Importer {

    /**
     * Import a scenario from a stream.
     * @param stream the stream
     * @return the imported scenario
     * @throws IOException on errors
     */
    Scenario importScenario( InputStream stream ) throws IOException;
}

