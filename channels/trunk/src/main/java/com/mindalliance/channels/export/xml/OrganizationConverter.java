package com.mindalliance.channels.export.xml;

import com.mindalliance.channels.model.Channel;
import com.mindalliance.channels.model.Job;
import com.mindalliance.channels.model.ModelEntity;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Place;
import com.mindalliance.channels.model.Scenario;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * XStream organization converter
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 16, 2009
 * Time: 6:53:05 PM
 */
public class OrganizationConverter extends EntityConverter {

    /**
     * Class logger.
     */
    public static final Logger LOG = LoggerFactory.getLogger( OrganizationConverter.class );


    public OrganizationConverter( XmlStreamer.Context context ) {
        super( context );
    }

    /**
     * {@inheritDoc}
     */
    public boolean canConvert( Class aClass ) {
        return Organization.class.isAssignableFrom( aClass );
    }

    /**
     * {@inheritDoc}
     */
    ModelEntity findOrMakeEntity( String name, Long id, boolean importingPlan ) {
        return importingPlan
                ? getQueryService().findOrCreate( Organization.class, name, id )
                : getQueryService().findOrCreate( Organization.class, name );
    }

    /**
     * {@inheritDoc}
     */
    protected void writeSpecifics( ModelEntity entity,
                                   HierarchicalStreamWriter writer,
                                   MarshallingContext context ) {
        Organization org = (Organization) entity;
        Organization parent = org.getParent();
        if ( org.isActorsRequired() ) {
            writer.startNode( "actorsRequired" );
            writer.setValue( "true" );
            writer.endNode();
        }
        if ( org.isAgreementsRequired() ) {
            writer.startNode( "agreementsRequired" );
            writer.setValue( "true" );
            writer.endNode();
        }
        if ( parent != null && !parent.getName().trim().isEmpty() ) {
            writer.startNode( "parent" );
            writer.addAttribute( "id", Long.toString( parent.getId() ) );
            writer.setValue( parent.getName() );
            writer.endNode();
        }
        Place location = org.getLocation();
        if ( location != null && !location.getName().trim().isEmpty() ) {
            writer.startNode( "location" );
            writer.addAttribute( "id", Long.toString( location.getId() ) );
            writer.setValue( location.getName() );
            writer.endNode();
        }
        // channels
        for ( Channel channel : org.getChannels() ) {
            writer.startNode( "channel" );
            context.convertAnother( channel );
            writer.endNode();
        }
        // jobs
        for ( Job job : org.getJobs() ) {
            writer.startNode( "job" );
            context.convertAnother( job );
            writer.endNode();
        }
    }

    /**
     * {@inheritDoc}
     */
    protected void setSpecific( ModelEntity entity,
                                String nodeName,
                                HierarchicalStreamReader reader,
                                UnmarshallingContext context ) {
        Scenario scenario = (Scenario) context.get( "scenario" );
        Organization org = (Organization) entity;
        if ( nodeName.equals( "actorsRequired" ) ) {
            org.setActorsRequired( reader.getValue().equals( "true" ) );
        } else if ( nodeName.equals( "agreementsRequired" ) ) {
            org.setAgreementsRequired( reader.getValue().equals( "true" ) );
        } else if ( nodeName.equals( "parent" ) ) {
            String id = reader.getAttribute( "id");
            org.setParent( findOrCreate( Organization.class, reader.getValue(), id ) );
        } else if ( nodeName.equals( "location" ) ) {
            String id = reader.getAttribute( "id");
            org.setLocation( findOrCreate( Place.class, reader.getValue(), id ) );
        } else if ( nodeName.equals( "channel" ) ) {
            Channel channel = (Channel) context.convertAnother( scenario, Channel.class );
            org.addChannel( channel );
        } else if ( nodeName.equals( "job" ) ) {
            Job job = (Job) context.convertAnother( scenario, Job.class );
            org.addJob( job );
        } else {
            LOG.warn( "Unknown element " + nodeName );
        }
    }
}
