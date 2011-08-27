package com.mindalliance.channels.engine.export.xml;

import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Channel;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.Participation;
import com.mindalliance.channels.core.model.Plan;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 19, 2010
 * Time: 3:10:36 PM
 */
public class ParticipationConverter extends EntityConverter {

    /**
     * Class logger.
     */
    public static final Logger LOG = LoggerFactory.getLogger( ActorConverter.class );

    public ParticipationConverter( XmlStreamer.Context context ) {
        super( context );
    }

    public boolean canConvert( Class aClass ) {
        return Participation.class.isAssignableFrom( aClass );
    }

    /**
     * {@inheritDoc}
     */
    protected Class<? extends ModelEntity> getEntityClass() {
        return Participation.class;
    }

    /**
     * {@inheritDoc}
     */
    protected void writeSpecifics( ModelEntity entity,
                                   HierarchicalStreamWriter writer,
                                   MarshallingContext context ) {
        Participation participation = (Participation) entity;
        if ( participation.getUsername() != null ) {
            writer.startNode( "user" );
            writer.setValue( participation.getUsername() );
            writer.endNode();
        }
        if ( participation.getActor() != null ) {
            Actor actor = participation.getActor();
            writer.startNode( "actor" );
            writer.addAttribute( "id", Long.toString( actor.getId() ) );
            writer.setValue( actor.getName() );
            writer.endNode();
        }
        // channels
        for ( Channel channel : participation.getChannels() ) {
            writer.startNode( "channel" );
            context.convertAnother( channel );
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
        Participation participation = (Participation) entity;
        if ( nodeName.equals( "actor" ) ) {
            Long id = Long.parseLong( reader.getAttribute( "id" ) );
            String name = reader.getValue();
            Actor actor = getEntity( Actor.class, name, id, ModelEntity.Kind.Actual, context );
            participation.setActor( actor );
        } else if ( nodeName.equals( "channel" ) ) {
            Channel channel = (Channel) context.convertAnother( plan, Channel.class );
            participation.addChannel( channel );
        } else if ( nodeName.equals( "user" ) ) {
            participation.setUsername( reader.getValue() );
        } else {
            LOG.warn( "Unknown element " + nodeName );
        }
    }

}
