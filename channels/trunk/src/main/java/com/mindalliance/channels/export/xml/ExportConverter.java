package com.mindalliance.channels.export.xml;

import com.mindalliance.channels.Channels;
import com.mindalliance.channels.Exporter;
import com.mindalliance.channels.model.Scenario;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;


/**
 * Dummy converter for restoring removed scenarios.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 25, 2009
 * Time: 9:09:27 AM
 */
public class ExportConverter extends AbstractChannelsConverter {

    public ExportConverter( Exporter exporter ) {
        super( exporter );
    }

    /**
     * {@inheritDoc}
     */
    public void marshal( Object o, HierarchicalStreamWriter hierarchicalStreamWriter, MarshallingContext marshallingContext ) {
        // Do nothing
    }

    /**
     * {@inheritDoc}
     */
    public Object unmarshal( HierarchicalStreamReader reader, UnmarshallingContext context ) {
        Object results = null;
        while ( reader.hasMoreChildren() ) {
            reader.moveDown();
            String nodeName = reader.getNodeName();
            if ( nodeName.equals( "scenario" ) ) {
                results = context.convertAnother( Channels.getPlan(), Scenario.class );
            }
        }
        return results;
    }

    /**
     * {@inheritDoc}
     */
    public boolean canConvert( Class aClass ) {
        return aClass.isAssignableFrom( Export.class );
    }
}
