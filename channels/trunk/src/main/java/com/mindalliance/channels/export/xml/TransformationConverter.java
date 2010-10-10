package com.mindalliance.channels.export.xml;

import com.mindalliance.channels.model.Transformation;
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
        for ( String subject : transformation.getSubjects() ) {
            writer.startNode( "subject" );
            writer.setValue( subject );
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
                String subject = reader.getValue();
                transformation.addSubject( subject );
            }
            reader.moveUp();
        }
        return transformation;
    }

}
