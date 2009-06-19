package com.mindalliance.channels.export.xml;

import com.mindalliance.channels.Exporter;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Job;
import com.mindalliance.channels.model.Place;
import com.mindalliance.channels.model.Role;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Job XML converter.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 26, 2009
 * Time: 9:38:21 AM
 */
public class JobConverter extends AbstractChannelsConverter {

    public JobConverter( Exporter exporter ) {
        super( exporter );
    }

    /**
     * Class logger.
     */
    public static final Logger LOG = LoggerFactory.getLogger( JobConverter.class );


    public boolean canConvert( Class aClass ) {
        return Job.class.isAssignableFrom( aClass );
    }

    public void marshal(
            Object obj,
            HierarchicalStreamWriter writer,
            MarshallingContext context ) {
        Job job = (Job) obj;
        writer.startNode( "actor" );
        writer.addAttribute( "id", Long.toString( job.getActor().getId() ) );
        writer.setValue( job.getActorName() );
        writer.endNode();
        writer.startNode( "role" );
        writer.addAttribute( "id", Long.toString( job.getRole().getId() ) );
        writer.setValue( job.getRoleName() );
        writer.endNode();
        if ( job.getJurisdiction() != null ) {
            writer.startNode( "jurisdiction" );
            writer.addAttribute( "id", Long.toString( job.getJurisdiction().getId() ) );
            writer.setValue( job.getJurisdictionName() );
            writer.endNode();
        }
        writer.startNode( "title" );
        writer.setValue( job.getTitle() );
        writer.endNode();
    }

    public Object unmarshal(
            HierarchicalStreamReader reader,
            UnmarshallingContext context ) {
        Job job = new Job();
        while ( reader.hasMoreChildren() ) {
            reader.moveDown();
            String nodeName = reader.getNodeName();
            if ( nodeName.equals( "actor" ) ) {
                String id = reader.getAttribute( "id" );
                job.setActor( findOrCreate( Actor.class, reader.getValue(), id ) );
            } else if ( nodeName.equals( "role" ) ) {
                String id = reader.getAttribute( "id" );
                job.setRole( findOrCreate( Role.class, reader.getValue(), id ) );
            } else if ( nodeName.equals( "jurisdiction" ) ) {
                String id = reader.getAttribute( "id" );
                job.setJurisdiction( findOrCreate( Place.class, reader.getValue(), id ) );
            } else if ( nodeName.equals( "title" ) ) {
                job.setTitle( reader.getValue() );
            } else {
                LOG.warn( "Unknown element " + nodeName );
            }
            reader.moveUp();
        }
        return job;
    }

}
