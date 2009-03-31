package com.mindalliance.channels.export.xml;

import com.mindalliance.channels.Role;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.pages.Project;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;

/**
 * XStream Role converter
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 16, 2009
 * Time: 6:52:06 PM
 */
public class RoleConverter extends EntityConverter {

    public RoleConverter() {
    }

    public boolean canConvert( Class aClass ) {
        return Role.class.isAssignableFrom( aClass );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    ModelObject findOrMakeEntity( String name ) {
        return Project.dqo().findOrCreate( Role.class, name );
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
    protected void setSpecific(
            ModelObject entity,
            String nodeName,
            HierarchicalStreamReader reader,
            UnmarshallingContext context  ) {
       // Do nothing
    }
}
