package com.mindalliance.channels.core.export.xml;

import com.mindalliance.channels.core.model.AssignedLocation;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.Subject;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * XML converter for assigned location.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/19/11
 * Time: 11:06 AM
 */
public class AssignedLocationConverter  extends AbstractChannelsConverter {

    /**
     * Class logger.
     */
    public static final Logger LOG = LoggerFactory.getLogger( AssignedLocationConverter.class );


    public AssignedLocationConverter( XmlStreamer.Context context ) {
        super( context );
    }

    @Override
    public boolean canConvert( Class type ) {
        return AssignedLocation.class.isAssignableFrom( type );
    }

    @Override
    public void marshal( Object object, HierarchicalStreamWriter writer, MarshallingContext context ) {
        AssignedLocation assignedLocation = (AssignedLocation)object;
        writer.addAttribute( "kind", assignedLocation.getKind().name() );
        if ( assignedLocation.isNamed() ) {
            Place place = assignedLocation.getNamedPlace();
            writer.startNode( "place" );
            writer.addAttribute( "kind", place.isType() ? "Type" : "Actual" );
            writer.addAttribute( "id", Long.toString( place.getId() ) );
            writer.setValue( place.getName() );
            writer.endNode();
        }
        if ( assignedLocation.isCommunicated() ) {
            Subject subject = assignedLocation.getSubject();
            writer.startNode( "subject" );
            writer.startNode( "info" );
            writer.setValue( subject.getInfo() );
            writer.endNode();
            writer.startNode( "eoi" );
            writer.setValue( subject.getContent() );
            writer.endNode();
            writer.endNode();
        }
    }

    @Override
    public Object unmarshal( HierarchicalStreamReader reader, UnmarshallingContext context ) {
        AssignedLocation assignedLocation = new AssignedLocation();
        assignedLocation.setKind( AssignedLocation.Kind.valueOf( reader.getAttribute( "kind" ) ) );
         while ( reader.hasMoreChildren() ) {
            reader.moveDown();
            String nodeName = reader.getNodeName();
            if ( nodeName.equals( "place" ) ) {
                ModelEntity.Kind kind = kind( reader.getAttribute( "kind" ) );
                Long id =  Long.parseLong( reader.getAttribute( "id" ) );
                Place place = getEntity(
                        Place.class,
                        reader.getValue(),
                        id,
                        kind,
                        context );
                assignedLocation.setNamedPlace( place );
            } else if ( nodeName.equals( "subject" ) ) {
                String info = null;
                String eoi = null;
                while( reader.hasMoreChildren() ) {
                    reader.moveDown();
                    String subNodeName = reader.getNodeName();
                    if ( subNodeName.equals( "info" ) ) {
                        info = reader.getValue();
                    } else if ( subNodeName.equals( "eoi" ) ) {
                        eoi = reader.getValue();
                    } else {
                        LOG.debug( "Unknown element " + subNodeName );
                    }
                    reader.moveUp();
                }
                Subject subject = new Subject( info, eoi );
                assignedLocation.setSubject( subject );
            } else {
                LOG.debug( "Unknown element " + nodeName );
            }
            reader.moveUp();
         }
        return assignedLocation;
    }

}
