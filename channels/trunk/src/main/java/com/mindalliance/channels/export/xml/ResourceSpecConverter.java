package com.mindalliance.channels.export.xml;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.mindalliance.channels.ResourceSpec;
import com.mindalliance.channels.Actor;
import com.mindalliance.channels.Role;
import com.mindalliance.channels.Organization;
import com.mindalliance.channels.Place;
import com.mindalliance.channels.Channel;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.pages.Project;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 21, 2009
 * Time: 3:05:13 PM
 */
public class ResourceSpecConverter implements Converter {
    /**
     * {@inheritDoc}
     */
    public boolean canConvert( Class aClass ) {
        return ResourceSpec.class.isAssignableFrom( aClass );
    }

    /**
     * {@inheritDoc}
     */
    public void marshal( Object object, HierarchicalStreamWriter writer, MarshallingContext context ) {
        ResourceSpec resourceSpec = (ResourceSpec) object;
        if ( !resourceSpec.isAnyActor() ) {
            writer.startNode( "actor" );
            writer.setValue( resourceSpec.getActor().getName() );
            writer.endNode();
        }
        if ( !resourceSpec.isAnyRole() ) {
            writer.startNode( "role" );
            writer.setValue( resourceSpec.getRole().getName() );
            writer.endNode();
        }
        if ( !resourceSpec.isAnyOrganization() ) {
            writer.startNode( "organization" );
            writer.setValue( resourceSpec.getOrganization().getName() );
            writer.endNode();
        }
        if ( !resourceSpec.isAnyJurisdiction() ) {
            writer.startNode( "jurisdiction" );
            writer.setValue( resourceSpec.getJurisdiction().getName() );
            writer.endNode();
        }
        // channels
        for ( Channel channel : resourceSpec.getChannels() ) {
            writer.startNode( "channel" );
            context.convertAnother( channel );
            writer.endNode();
        }
    }

    /**
     * {@inheritDoc}
     */
    public Object unmarshal( HierarchicalStreamReader reader, UnmarshallingContext context ) {
        ResourceSpec resourceSpec = new ResourceSpec();
        Scenario scenario = (Scenario) context.get( "scenario" );
        while ( reader.hasMoreChildren() ) {
            reader.moveDown();
            String nodeName = reader.getNodeName();
            if ( nodeName.equals( "actor" ) ) {
                resourceSpec.setActor(
                        Project.service().findOrCreate(
                                Actor.class, reader.getValue() ) );
            } else if ( nodeName.equals( "role" ) ) {
                resourceSpec.setRole(
                        Project.service().findOrCreate(
                                Role.class, reader.getValue() ) );
            } else if ( nodeName.equals( "organization" ) ) {
                resourceSpec.setOrganization(
                        Project.service().findOrCreate(
                                Organization.class, reader.getValue() ) );
            } else if ( nodeName.equals( "jurisdiction" ) ) {
                resourceSpec.setJurisdiction(
                        Project.service().findOrCreate(
                                Place.class, reader.getValue() ) );
            } else if ( nodeName.equals( "channel" ) ) {
                Channel channel = (Channel) context.convertAnother( scenario, Channel.class );
                resourceSpec.addChannel( channel );
            } else {
                throw new ConversionException( "Unknown element " + nodeName );
            }
            reader.moveUp();
        }
        return resourceSpec;
    }

}
