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
        writer.setValue( job.getActorName() );
        writer.endNode();
        writer.startNode( "role" );
        writer.setValue( job.getRoleName() );
        writer.endNode();
        writer.startNode( "jurisdiction" );
        writer.setValue( job.getJurisdictionName() );
        writer.endNode();
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
                job.setActor( getQueryService().findOrCreate( Actor.class, reader.getValue() ) );
            } else if ( nodeName.equals( "role" ) ) {
                job.setRole( getQueryService().findOrCreate( Role.class, reader.getValue() ) );
            } else if ( nodeName.equals( "jurisdiction" ) ) {
                job.setJurisdiction( getQueryService().findOrCreate( Place.class, reader.getValue() ) );
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
