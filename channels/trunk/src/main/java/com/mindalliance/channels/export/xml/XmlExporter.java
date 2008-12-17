package com.mindalliance.channels.export.xml;

import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.export.xml.ConfiguredXStream;
import com.mindalliance.channels.export.Exporter;
import com.thoughtworks.xstream.XStream;

import java.io.OutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Dec 16, 2008
 * Time: 1:04:00 PM
 */
public class XmlExporter implements Exporter {

    public XmlExporter() { }
    
    /**
     * Export a scenario as XML on the given stream.
     *
     * @param scenario the scenario
     * @param stream   the stream
     * @throws java.io.IOException on errors
     */
    public void exportScenario( Scenario scenario, OutputStream stream ) throws IOException {
        XStream xstream = ConfiguredXStream.getNew();
        ObjectOutputStream out = xstream.createObjectOutputStream(stream, "export");
        out.writeObject(scenario);
        out.close();
    }
}
