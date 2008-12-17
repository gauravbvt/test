package com.mindalliance.channels.export.xml;

import com.mindalliance.channels.export.Importer;
import com.mindalliance.channels.Scenario;
import com.thoughtworks.xstream.XStream;

import java.io.InputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Dec 16, 2008
 * Time: 9:12:25 PM
 */
public class XmlImporter implements Importer {

    public XmlImporter() { }

    /**
     * Import a scenario from a stream.
     *
     * @param stream the stream
     * @return the imported scenario
     * @throws java.io.IOException on errors
     */
    public Scenario importScenario( InputStream stream ) throws IOException {
        XStream xstream = ConfiguredXStream.getNew();
        ObjectInputStream in = xstream.createObjectInputStream( stream );
        Scenario scenario = null;
        try {
            scenario = (Scenario)in.readObject();
        } catch ( ClassNotFoundException e ) {
            throw new IOException("Failed to import scenario", e);
        }
        return scenario;
    }
}
