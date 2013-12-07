package com.mindalliance.channels.core.export.xml;

import com.mindalliance.channels.core.IssueDetectionWaiver;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/6/13
 * Time: 1:08 PM
 */
public class IssueDetectionWaiverConverter extends AbstractChannelsConverter {

    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( IssueDetectionWaiverConverter.class );
    public IssueDetectionWaiverConverter( XmlStreamer.Context context ) {
        super( context );
    }

    @Override
    public boolean canConvert( Class aClass ) {
        return IssueDetectionWaiver.class.isAssignableFrom( aClass );
    }


    @Override
    public void marshal( Object object, HierarchicalStreamWriter writer, MarshallingContext context ) {
        IssueDetectionWaiver waiver = (IssueDetectionWaiver)object;
        writer.startNode( "identifiableUid" );
        writer.setValue( waiver.getIdentifiableUid() );
        writer.endNode();
        writer.startNode( "identifiableName" );
        writer.setValue( waiver.getIdentifiableName()  );
        writer.endNode();
        writer.startNode( "identifiableTypeName" );
        writer.setValue( waiver.getIdentifiableTypeName()  );
        writer.endNode();
        writer.startNode( "detector" );
        writer.setValue( waiver.getDetector()  );
        writer.endNode();
    }

    @Override
    public Object unmarshal( HierarchicalStreamReader reader, UnmarshallingContext context ) {
        IssueDetectionWaiver waiver = new IssueDetectionWaiver(  );
        while ( reader.hasMoreChildren() ) {
            reader.moveDown();
            String nodeName = reader.getNodeName();
            if ( nodeName.equals( "identifiableUid" ) ) {
                waiver.setIdentifiableUid( reader.getValue() );
            } else if ( nodeName.equals( "identifiableTypeName" ) ) {
                waiver.setIdentifiableTypeName( reader.getValue() );
            } else if ( nodeName.equals( "identifiableName" ) ) {
                waiver.setIdentifiableName( reader.getValue() );
            } else if ( nodeName.equals( "detector" ) ) {
                waiver.setDetector( reader.getValue() );
            } else {
                LOG.debug( "Unknown element " + nodeName );
            }
            reader.moveUp();
        }
        return waiver;
    }

}
