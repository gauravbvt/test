package com.mindalliance.channels.core.export.xml;

import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Availability;
import com.mindalliance.channels.core.model.Channel;
import com.mindalliance.channels.core.model.Classification;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.CollaborationModel;
import com.mindalliance.channels.core.model.WorkTime;
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
        // open participation
        writer.startNode( "openParticipation" );
        writer.setValue( Boolean.toString( actor.isOpenParticipation() ) );
        writer.endNode();
        // max participation
        writer.startNode( "maxParticipation" );
        writer.setValue( Integer.toString( actor.getMaxParticipation() ) );
        writer.endNode();
        // participation restricted to already employed
        writer.startNode( "participationRestrictedToEmployed" );
        writer.setValue( Boolean.toString( actor.isParticipationRestrictedToEmployed() ) );
        writer.endNode();
        // participation must be confirmed by a supervisor
        writer.startNode( "supervisedParticipation" );
        writer.setValue( Boolean.toString( actor.isSupervisedParticipation() ) );
        writer.endNode();
        // participant identity visibility
        writer.startNode( "anonymousParticipation" );
        writer.setValue( Boolean.toString( actor.isAnonymousParticipation() ) );
        writer.endNode();
        // availability
        if ( actor.getAvailability() != null ) {
            writer.startNode( "workPeriod" );
            writer.setValue( actor.getAvailability().getWorkPeriod().name() );
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
        // languages spoken
        for ( String lang : actor.getLanguages() ) {
            writer.startNode( "language" );
            context.convertAnother( lang );
            writer.endNode();
        }
    }

    /**
     * {@inheritDoc}
     */
    protected void setSpecific( ModelEntity entity, String nodeName,
                                HierarchicalStreamReader reader,
                                UnmarshallingContext context ) {
        CollaborationModel collaborationModel = getPlan();
        Actor actor = (Actor) entity;
        if ( nodeName.equals( "channel" ) ) {
            Channel channel = (Channel) context.convertAnother( collaborationModel, Channel.class );
            actor.addChannel( channel );
        } else if ( nodeName.equals( "system" ) ) {
            boolean isSystem = reader.getValue().equals( "true" );
            actor.setSystem( isSystem );
        } else if ( nodeName.equals( "archetype" ) ) {   // obsolete
            boolean isArchetype = reader.getValue().equals( "true" );
            // actor.setArchetype( isArchetype );
            LOG.debug( "Obsolete element: isArchetype" );
            if ( isArchetype ) {
                actor.setSingularParticipation( false );
                actor.setAnonymousParticipation( false );
            }
        } else if ( nodeName.equals( "placeHolder" ) ) { // obsolete
            LOG.debug( "Obsolete element: placeHolder" );
            if ( reader.getAttributeCount() > 0 ) {
                boolean singular = Boolean.parseBoolean( reader.getAttribute( "singular" ) );
                actor.setSingularParticipation( singular );
                // actor.setPlaceHolderSingular( singular );
            }
            // boolean val = reader.getValue().equals( "true" );
            // actor.setPlaceHolder( val );
        } else if ( nodeName.equals( "openParticipation" ) ) {
            actor.setOpenParticipation( reader.getValue().equals( "true" ) );
        } else if ( nodeName.equals( "singularParticipation" ) ) {     // todo - obsolete
            actor.setSingularParticipation( reader.getValue().equals( "true" ) );
        } else if ( nodeName.equals( "maxParticipation" ) ) {
            actor.setMaxParticipation( Integer.parseInt( reader.getValue() ) );
        } else if ( nodeName.equals( "participationRestrictedToEmployed" ) ) {
            actor.setParticipationRestrictedToEmployed( reader.getValue().equals( "true" ) );
        } else if ( nodeName.equals( "supervisedParticipation" ) ) {
            actor.setSupervisedParticipation( reader.getValue().equals( "true" ) );
        } else if ( nodeName.equals( "anonymousParticipation" ) ) {
            actor.setAnonymousParticipation( reader.getValue().equals( "true" ) );
        } else if ( nodeName.equals( "clearance" ) ) {
            Classification clearance = (Classification) context.convertAnother( collaborationModel, Classification.class );
            actor.addClearance( clearance );
        } else if ( nodeName.equals( "language" ) ) {
            actor.addLanguage( reader.getValue() );
        } else if ( nodeName.equals( "workPeriod" ) ) {
            WorkTime availability = new WorkTime( WorkTime.WorkPeriod.valueOf( reader.getValue() ) );
            actor.setAvailability( availability );
        } else if ( nodeName.equals( "availability" ) ) { // OBSOLETE - conversion
            Availability availability = (Availability) context.convertAnother( collaborationModel, Availability.class );
            actor.setAvailability( availability.isAlways() ? WorkTime.FullTime() : WorkTime.PartTime() );
        } else {
            LOG.debug( "Unknown element " + nodeName );
        }
    }

}
