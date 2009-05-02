package com.mindalliance.channels.export.xml;

import com.mindalliance.channels.Exporter;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Place;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * XStream place converter
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 16, 2009
 * Time: 6:54:07 PM
 */
public class PlaceConverter extends EntityConverter {

    public PlaceConverter( Exporter exporter ) {
        super( exporter );
    }

    /**
     * {@inheritDoc}
     */
    public boolean canConvert( Class aClass ) {
        return Place.class.isAssignableFrom( aClass );
    }

    /**
     * {@inheritDoc}
     */
    ModelObject findOrMakeEntity( String name ) {
        return getQueryService().findOrCreate( Place.class, name );
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
