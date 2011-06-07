package com.mindalliance.channels.export.xml;

import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Availability;
import com.mindalliance.channels.model.Channel;
import com.mindalliance.channels.model.Classification;
import com.mindalliance.channels.model.ModelEntity;
import com.mindalliance.channels.model.Plan;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * XStream Actor converter
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 16, 2009
 * Time: 6:42:21 PM
 */
public class ActorConverter extends EntityConverter {


    /**
     * Class logger.
     */
    public static final Logger LOG = LoggerFactory.getLogger( ActorConverter.class );

    public ActorConverter( XmlStreamer.Context context ) {
        super( context );
    }

    public boolean canConvert( Class aClass ) {
        return Actor.class.isAssignableFrom( aClass );
    }

    /**
     * {@inheritDoc}
     */
    protected Class<? extends ModelEntity> getEntityClass() {
        return Actor.class;
    }

    /**
     * {@inheritDoc}
     */
    protected void writeSpecifics( ModelEntity entity,
                                   HierarchicalStreamWriter writer,
                                   MarshallingContext context ) {
        Actor actor = (Actor) entity;
        if ( actor.isSystem() ) {
            writer.startNode( "system" );
            writer.setValue( "true" );
            writer.endNode();
        }
        if ( actor.isArchetype() ) {
            writer.startNode( "archetype" );
            writer.setValue( "true" );
            writer.endNode();
        }
        if ( actor.isPlaceHolder() ) {
            writer.startNode( "placeHolder" );
            writer.addAttribute( "singular", Boolean.toString( actor.isPlaceHolderSingular() ) );
            writer.setValue( "true" );
            writer.endNode();
        }
        if ( actor.getAvailability() != null ) {
            writer.startNode( "availability" );
            context.convertAnother( actor.getAvailability() );
            writer.endNode();
        }
        // channels
        for ( Channel channel : actor.getChannels() ) {
            writer.startNode( "channel" );
            context.convertAnother( channel );
            writer.endNode();
        }
        // classification clearances
        for ( Classification clearance : actor.getClearances() ) {
            writer.startNode( "clearance" );
            context.convertAnother( clearance );
            writer.endNode();
        }
    }

    /**
     * {@inheritDoc}
     */
    protected void setSpecific( ModelEntity entity, String nodeName,
                                HierarchicalStreamReader reader,
                                UnmarshallingContext context ) {
        Plan plan = getPlan();
        Actor actor = (Actor) entity;
        if ( nodeName.equals( "channel" ) ) {
            Channel channel = (Channel) context.convertAnother( plan, Channel.class );
            actor.addChannel( channel );
        } else if ( nodeName.equals( "system" ) ) {
            boolean isSystem = reader.getValue().equals( "true" );
            actor.setSystem( isSystem );
        } else if ( nodeName.equals( "archetype" ) ) {
            boolean isArchetype = reader.getValue().equals( "true" );
            actor.setArchetype( isArchetype );
        } else if ( nodeName.equals( "placeHolder" ) ) {
            boolean val = reader.getValue().equals( "true" );
            actor.setPlaceHolder( val );
            if ( reader.getAttributeCount() > 0 ) {
                actor.setPlaceHolderSingular( Boolean.parseBoolean( reader.getAttribute( "singular" ) ) );
            }
        } else if ( nodeName.equals( "clearance" ) ) {
            Classification clearance = (Classification) context.convertAnother( plan, Classification.class );
            actor.addClearance( clearance );
        } else if ( nodeName.equals( "availability" ) ) {
            Availability availability = (Availability) context.convertAnother( plan, Availability.class );
            actor.setAvailability(  availability );
        } else {
            LOG.warn( "Unknown element " + nodeName );
        }
    }

}
