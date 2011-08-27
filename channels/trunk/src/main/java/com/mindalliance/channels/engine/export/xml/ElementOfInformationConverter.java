package com.mindalliance.channels.engine.export.xml;

import com.mindalliance.channels.core.model.Classification;
import com.mindalliance.channels.core.model.ElementOfInformation;
import com.mindalliance.channels.core.model.Transformation;
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
        writer.startNode( "description" );
        writer.setValue( eoi.getDescription() );
        writer.endNode();
        writer.startNode( "specialHandlingCodes" );
        writer.setValue( eoi.getSpecialHandling() );
        writer.endNode();
        for ( Classification classification : eoi.getClassifications() ) {
            writer.startNode( "classification" );
            context.convertAnother( classification );
            writer.endNode();
        }
        if ( !eoi.getTransformation().isNone() ) {
            writer.startNode( "transformation" );
            context.convertAnother( eoi.getTransformation() );
            writer.endNode();
        }
        if ( eoi.isTimeSensitive() ) {
            writer.startNode( "timeSensitive" );
            writer.setValue( Boolean.toString( true ) );
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
                // todo -- "sourceCodes is obsolete - substituted by description
            } else if ( nodeName.equals( "sourceCodes" ) || nodeName.equals( "description" ) ) {
                eoi.setDescription( reader.getValue() );
            } else if ( nodeName.equals( "specialHandlingCodes" ) ) {
                eoi.setSpecialHandling( reader.getValue() );
            } else if ( nodeName.equals( "classification" ) ) {
                Classification classification = (Classification) context.convertAnother(
                        context.get( "segment" ),
                        Classification.class );
                eoi.addClassification( classification );
            } else if ( nodeName.equals( "transformation" ) ) {
                Transformation transformation = (Transformation) context.convertAnother(
                        context.get( "segment" ),
                        Transformation.class );
                eoi.setTransformation( transformation );
            } else if ( nodeName.equals( "timeSensitive" ) ) {
                eoi.setTimeSensitive( Boolean.valueOf( reader.getValue() ) );
            }
            reader.moveUp();
        }
        return eoi;
    }
}
