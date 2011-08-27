package com.mindalliance.channels.engine.export.xml;

import com.mindalliance.channels.core.model.Subject;
import com.mindalliance.channels.core.model.Transformation;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Oct 10, 2010
 * Time: 9:07:59 AM
 */
public class TransformationConverter  extends AbstractChannelsConverter {

    public TransformationConverter( XmlStreamer.Context context ) {
        super( context );
    }

    public boolean canConvert( Class type ) {
        return Transformation.class.isAssignableFrom( type );
    }
    public void marshal(
            Object source,
            HierarchicalStreamWriter writer,
            MarshallingContext context ) {
        Transformation transformation = (Transformation)source;
        Transformation.Type type = transformation.getType();
        writer.addAttribute( "type", type.name() );
        for ( Subject subject : transformation.getSubjects() ) {
            writer.startNode( "subject" );
            writer.startNode( "info" );
            writer.setValue( subject.getInfo() );
            writer.endNode();
            writer.startNode( "content" );
            writer.setValue( subject.getContent() );
            writer.endNode();
            writer.endNode();
        }
    }

    public Object unmarshal(
            HierarchicalStreamReader reader,
            UnmarshallingContext context ) {
        Transformation transformation = new Transformation();
        Transformation.Type type = Transformation.Type.valueOf( reader.getAttribute( "type" ));
        transformation.setType( type );
        while( reader.hasMoreChildren() ) {
            reader.moveDown();
            String nodeName = reader.getNodeName();
            if ( nodeName.equals( "subject" ) ) {
                Subject subject = transformation.newSubject();
                while( reader.hasMoreChildren() ) {
                    reader.moveDown();
                    String nn = reader.getNodeName();
                    if ( nn.equals( "info") ) {
                       subject.setInfo( reader.getValue() );
                    } else if ( nn.equals( "content" ) ) {
                       subject.setContent( reader.getValue() ); 
                    }
                    reader.moveUp();
                }
                transformation.addSubject( subject );
            }
            reader.moveUp();
        }
        return transformation;
    }

}
