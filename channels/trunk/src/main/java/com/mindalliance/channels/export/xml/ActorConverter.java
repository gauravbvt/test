package com.mindalliance.channels.export.xml;

import com.mindalliance.channels.Actor;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.Channel;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.pages.Project;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.UnmarshallingContext;

/**
 * XStream Actor converter
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 16, 2009
 * Time: 6:42:21 PM
 */
public class ActorConverter extends EntityConverter {

    public ActorConverter() {
    }

    public boolean canConvert( Class aClass ) {
        return Actor.class.isAssignableFrom( aClass );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    ModelObject findOrMakeEntity( String name ) {
        return Project.service().findOrCreate( Actor.class, name );
    }

    /**
     * {@inheritDoc}
     */
    protected void writeSpecifics( ModelObject entity,
                                   HierarchicalStreamWriter writer,
                                   MarshallingContext context ) {
        Actor actor = (Actor) entity;
        String jobTitle = actor.getJobTitle();
        if ( jobTitle != null && !jobTitle.trim().isEmpty() ) {
            writer.startNode( "jobTitle" );
            writer.setValue( jobTitle );
            writer.endNode();
        }
        // channels
        for ( Channel channel : actor.getChannels() ) {
            writer.startNode( "channel" );
            context.convertAnother( channel );
            writer.endNode();
        }
    }

    /**
     * {@inheritDoc}
     */
    protected void setSpecific( ModelObject entity, String nodeName,
                                HierarchicalStreamReader reader,
                                UnmarshallingContext context ) {
        Scenario scenario = (Scenario) context.get( "scenario" );
        Actor actor = (Actor) entity;
        if ( nodeName.equals( "jobTitle" ) ) {
            actor.setJobTitle( reader.getValue() );
        } else if ( nodeName.equals( "channel" ) ) {
            Channel channel = (Channel) context.convertAnother( scenario, Channel.class );
            actor.addChannel( channel );
        } else {
            throw new ConversionException( "Unknown element " + nodeName );
        }
    }

}
