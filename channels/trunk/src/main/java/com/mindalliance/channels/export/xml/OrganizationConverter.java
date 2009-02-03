package com.mindalliance.channels.export.xml;

import com.mindalliance.channels.Organization;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.pages.Project;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.converters.MarshallingContext;

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
       // Do nothing
    }

    /**
     * {@inheritDoc}
     */
    protected void setSpecific( ModelObject entity, String nodeName, String value ) {
       // Do nothing
    }
}
