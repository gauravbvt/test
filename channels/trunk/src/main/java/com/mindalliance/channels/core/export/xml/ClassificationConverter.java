package com.mindalliance.channels.core.export.xml;

import com.mindalliance.channels.core.model.Classification;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * Classification converter.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 3, 2009
 * Time: 9:41:54 AM
 */
public class ClassificationConverter extends AbstractChannelsConverter {

    public ClassificationConverter( XmlStreamer.Context context ) {
        super( context );
    }

    /**
     * {@inheritDoc}
     */
    public boolean canConvert( Class aClass ) {
        return Classification.class.isAssignableFrom( aClass );
    }

    /**
     * {@inheritDoc}
     */
    public void marshal( Object object, HierarchicalStreamWriter writer, MarshallingContext context ) {
        Classification classification = (Classification) object;
        writer.startNode( "system" );
        writer.setValue( classification.getSystem() );
        writer.endNode();
        writer.startNode( "name" );
        writer.setValue( classification.getName() );
        writer.endNode();
        writer.startNode( "level" );
        writer.setValue( classification.getLevel() + "" );
        writer.endNode();
    }

    /**
     * {@inheritDoc}
     */
    public Object unmarshal( HierarchicalStreamReader reader, UnmarshallingContext context ) {
        Classification classification = new Classification();
        while ( reader.hasMoreChildren() ) {
            reader.moveDown();
            String nodeName = reader.getNodeName();
            if ( nodeName.equals( "system" ) ) {
                classification.setSystem( reader.getValue() );
            } else if ( nodeName.equals( "name" ) ) {
                classification.setName( reader.getValue() );
            } else if ( nodeName.equals( "level" ) ) {
                classification.setLevel( Integer.parseInt( reader.getValue() ) );
            }
            reader.moveUp();
        }
        // Make sure all used classifications are registered with the plan.
        getModel().addClassification( classification );
        return classification;
    }

}
