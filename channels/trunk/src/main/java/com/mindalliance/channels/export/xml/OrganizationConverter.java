package com.mindalliance.channels.export.xml;

import com.mindalliance.channels.Organization;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.Place;
import com.mindalliance.channels.pages.Project;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.ConversionException;

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
        if (parent != null && !parent.getName().trim().isEmpty()) {
            writer.startNode( "parent" );
            writer.setValue( parent.getName() );
            writer.endNode();
        }
        Place location = org.getLocation();
        if (location != null && !location.getName().trim().isEmpty()) {
            writer.startNode( "location" );
            writer.setValue( location.getName() );
            writer.endNode();
        }
    }

    /**
     * {@inheritDoc}
     */
    protected void setSpecific( ModelObject entity, String nodeName, String value ) {
        if (nodeName.equals("parent")) {
            Organization org = (Organization)entity;
            org.setParent( Organization.named(value));
        }
        else if (nodeName.equals("location")) {
            Organization org = (Organization)entity;
            org.setLocation( Place.named(value));
        }
        else {
            throw new ConversionException( "Unknown element " + nodeName );
        }
    }
}
