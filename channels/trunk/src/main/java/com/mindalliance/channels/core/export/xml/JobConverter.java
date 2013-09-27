package com.mindalliance.channels.core.export.xml;

import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Job;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.Role;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Job XML converter.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 26, 2009
 * Time: 9:38:21 AM
 */
public class JobConverter extends AbstractChannelsConverter {

    public JobConverter( XmlStreamer.Context context ) {
        super( context );
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
            writer.addAttribute( "kind", job.getJurisdiction().isType() ? "Type" : "Actual" );
            writer.setValue( job.getJurisdictionName() );
            writer.endNode();
        }
        writer.startNode( "title" );
        writer.setValue( job.getTitle() );
        writer.endNode();
        if ( job.getSupervisor() != null ) {
            writer.startNode( "supervisor" );
            writer.addAttribute( "id", Long.toString( job.getSupervisor().getId() ) );
            writer.setValue( job.getSupervisorName() );
            writer.endNode();
        }
        writer.startNode( "linked" );
        writer.setValue( Boolean.toString( job.isLinked() ) );
        writer.endNode();
    }

    public Object unmarshal(
            HierarchicalStreamReader reader,
            UnmarshallingContext context ) {
        Map<Long, Long> idMap = getIdMap( context );
        boolean importingPlan = isImportingPlan( context );
        Job job = new Job();
        while ( reader.hasMoreChildren() ) {
            reader.moveDown();
            String nodeName = reader.getNodeName();
            if ( nodeName.equals( "actor" ) ) {
                String id = reader.getAttribute( "id" );
                Actor actor = getEntity(
                        Actor.class,
                        reader.getValue(),
                        Long.parseLong( id ),
                        false,
                        importingPlan,
                        idMap );
                job.setActor( actor );
            } else if ( nodeName.equals( "role" ) ) {
                String id = reader.getAttribute( "id" );
                Role role = getEntity(
                        Role.class,
                        reader.getValue(),
                        Long.parseLong( id ),
                        true,
                        importingPlan,
                        idMap );
                job.setRole( role );
            } else if ( nodeName.equals( "jurisdiction" ) ) {
                String id = reader.getAttribute( "id" );
                ModelEntity.Kind kind = kind( reader.getAttribute( "kind" ) );
                Place jurisdiction = getEntity(
                        Place.class,
                        reader.getValue(),
                        Long.parseLong( id ),
                        kind,
                        context );
                /*Place jurisdiction = getEntity(
                        Place.class,
                        reader.getValue(),
                        Long.parseLong( id ),
                        false,
                        importingPlan,
                        idMap );*/
                job.setJurisdiction( jurisdiction );
            } else if ( nodeName.equals( "title" ) ) {
                job.setTitle( reader.getValue() );
            } else if ( nodeName.equals( "supervisor" ) ) {
                String id = reader.getAttribute( "id" );
                Actor supervisor = getEntity(
                        Actor.class,
                        reader.getValue(),
                        Long.parseLong( id ),
                        false,
                        importingPlan,
                        idMap );
                job.setSupervisor( supervisor );
            } else if ( nodeName.equals( "linked" ) ) {
                job.setLinked( Boolean.parseBoolean( reader.getValue() ) );
            } else {
                LOG.debug( "Unknown element " + nodeName );
            }
            reader.moveUp();
        }
        return job;
    }

}
