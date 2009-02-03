package com.mindalliance.channels.export.xml;

import com.mindalliance.channels.Actor;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.pages.Project;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.converters.MarshallingContext;

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
       // Do nothing
    }

    /**
     * {@inheritDoc}
     */
    protected void setSpecific( ModelObject entity, String nodeName, String value ) {
       // Do nothing
    }

  }
