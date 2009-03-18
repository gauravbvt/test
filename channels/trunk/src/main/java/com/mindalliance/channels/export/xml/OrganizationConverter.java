package com.mindalliance.channels.export.xml;

import com.mindalliance.channels.Organization;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.Place;
import com.mindalliance.channels.Channel;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.pages.Project;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.UnmarshallingContext;

/**
 * XStream organization converter
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 16, 2009
 * Time: 6:53:05 PM
 */
public class OrganizationConverter extends EntityConverter {

    public OrganizationConverter() {
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
    ModelObject findOrMakeEntity( String name ) {
        return Project.service().findOrCreate( Organization.class, name );
    }

    /**
     * {@inheritDoc}
     */
    protected void writeSpecifics( ModelObject entity,
                                   HierarchicalStreamWriter writer,
                                   MarshallingContext context ) {
        Organization org = (Organization) entity;
        Organization parent = org.getParent();
        if ( parent != null && !parent.getName().trim().isEmpty() ) {
            writer.startNode( "parent" );
            writer.setValue( parent.getName() );
            writer.endNode();
        }
        Place location = org.getLocation();
        if ( location != null && !location.getName().trim().isEmpty() ) {
            writer.startNode( "location" );
            writer.setValue( location.getName() );
            writer.endNode();
        }
        // channels
        for ( Channel channel : org.getChannels() ) {
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
        Organization org = (Organization) entity;
        if ( nodeName.equals( "parent" ) ) {
            org = (Organization) entity;
            org.setParent( Project.service().findOrCreate( Organization.class, reader.getValue() ) );
        } else if ( nodeName.equals( "location" ) ) {
            org = (Organization) entity;
            org.setLocation( Project.service().findOrCreate( Place.class, reader.getValue() ) );
        } else if ( nodeName.equals( "channel" ) ) {
            Channel channel = (Channel) context.convertAnother( scenario, Channel.class );
            org.addChannel( channel );
        } else {
            throw new ConversionException( "Unknown element " + nodeName );
        }
    }
}
