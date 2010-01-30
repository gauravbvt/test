package com.mindalliance.channels.export.xml;

import com.mindalliance.channels.model.Classification;
import com.mindalliance.channels.model.ElementOfInformation;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * Element of information converter.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 3, 2009
 * Time: 10:24:29 AM
 */
public class ElementOfInformationConverter extends AbstractChannelsConverter {

    public ElementOfInformationConverter( XmlStreamer.Context context ) {
        super( context );
    }

    /**
     * {@inheritDoc}
     */
    public boolean canConvert( Class aClass ) {
        return ElementOfInformation.class.isAssignableFrom( aClass );
    }

    /**
     * {@inheritDoc}
     */
    public void marshal( Object object, HierarchicalStreamWriter writer, MarshallingContext context ) {
        ElementOfInformation eoi = (ElementOfInformation) object;
        writer.startNode( "content" );
        writer.setValue( eoi.getContent() );
        writer.endNode();
        writer.startNode( "sourceCodes" );
        writer.setValue( eoi.getSources() );
        writer.endNode();
        writer.startNode( "specialHandlingCodes" );
        writer.setValue( eoi.getSpecialHandling() );
        writer.endNode();
        for ( Classification classification : eoi.getClassifications() ) {
            writer.startNode( "classification" );
            context.convertAnother( classification );
            writer.endNode();
        }
    }

    /**
     * {@inheritDoc}
     */
    public Object unmarshal( HierarchicalStreamReader reader, UnmarshallingContext context ) {
        ElementOfInformation eoi = new ElementOfInformation();
        while ( reader.hasMoreChildren() ) {
            reader.moveDown();
            String nodeName = reader.getNodeName();
            if ( nodeName.equals( "content" ) ) {
                eoi.setContent( reader.getValue() );
            } else if ( nodeName.equals( "sourceCodes" ) ) {
                eoi.setSources( reader.getValue() );
            } else if ( nodeName.equals( "specialHandlingCodes" ) ) {
                eoi.setSpecialHandling( reader.getValue() );
            } else if ( nodeName.equals( "classification" ) ) {
                Classification classification = (Classification) context.convertAnother(
                        context.get( "segment" ),
                        Classification.class );
                eoi.addClassification( classification );
            }
            reader.moveUp();
        }
        return eoi;
    }
}
