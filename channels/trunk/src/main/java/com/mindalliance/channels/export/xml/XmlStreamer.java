package com.mindalliance.channels.export.xml;

import com.mindalliance.channels.export.Importer;
import com.mindalliance.channels.export.Exporter;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.Flow;
import com.mindalliance.channels.Role;
import com.mindalliance.channels.Actor;
import com.mindalliance.channels.Location;
import com.mindalliance.channels.Jurisdiction;
import com.thoughtworks.xstream.XStream;

import java.io.InputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.ObjectOutputStream;

/**
 * XML scenario importer.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Dec 16, 2008
 * Time: 9:12:25 PM
 */
public class XmlStreamer implements Importer, Exporter {
    /**
     * Holder of a configured XStream instance.
     */
    private class ConfiguredXStream {

        /**
         * The xstream instance
         */
        private XStream xstream;

        /**
         * Constructor
         */
        public ConfiguredXStream() {
            xstream = new XStream();
            configure();
        }

        /**
         * Configuration of xstream instance
         */
        private void configure() {
            xstream.aliasType( "scenario", Scenario.class );
            xstream.alias( "part", Part.class );
            xstream.aliasType( "flow", Flow.class );
            xstream.alias( "role", Role.class );
            xstream.alias( "actor", Actor.class );
            xstream.alias( "location", Location.class );
            xstream.alias( "jurisdiction", Jurisdiction.class );
            xstream.registerConverter( new ScenarioConverter() );
            xstream.registerConverter( new PartConverter() );
            xstream.registerConverter( new FlowConverter() );
        }

        /**
         * Access to xstream instance
         * @return -- an xstream
         */
        public XStream get() {
            return xstream;
        }
    }

    /**
     * Configured xstream
     */
    private ConfiguredXStream configuredXStream;

    /**
     * Constructor
     */
    public XmlStreamer() {
        configuredXStream = new ConfiguredXStream();
    }

    /**
     * {@inheritDoc}
     */
    public Scenario importScenario( InputStream stream ) throws IOException {
        ObjectInputStream in = configuredXStream.get().createObjectInputStream( stream );
        Scenario scenario;
        try {
            scenario = (Scenario) in.readObject();
        } catch ( ClassNotFoundException e ) {
            throw new IOException( "Failed to import scenario", e );
        }
        return scenario;
    }

    /**
     * {@inheritDoc}
     */
    public void exportScenario( Scenario scenario, OutputStream stream ) throws IOException {
        ObjectOutputStream out = configuredXStream.get()
                .createObjectOutputStream( stream, "export" );
        out.writeObject( scenario );
        out.close();
    }


    /**
     * {@inheritDoc}
     */
    public String getMimeType() {
        return "application/xml";
    }
}
