package com.mindalliance.channels.engine.export.xml;

import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.Role;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * XStream Role converter
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 16, 2009
 * Time: 6:52:06 PM
 */
public class RoleConverter extends EntityConverter {

    public RoleConverter( XmlStreamer.Context context ) {
        super( context );
    }

    public boolean canConvert( Class aClass ) {
        return Role.class.isAssignableFrom( aClass );
    }

    /**
     * {@inheritDoc}
     */
    protected Class<? extends ModelEntity> getEntityClass() {
        return Role.class;
    }

    /**
     * {@inheritDoc}
     */
    protected void writeSpecifics( ModelEntity entity,
                                   HierarchicalStreamWriter writer,
                                   MarshallingContext context ) {
       // Do nothing
    }

    /**
     * {@inheritDoc}
     */
    protected void setSpecific(
            ModelEntity entity,
            String nodeName,
            HierarchicalStreamReader reader,
            UnmarshallingContext context  ) {
       // Do nothing
    }


}
