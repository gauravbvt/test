package com.mindalliance.channels.core.export.xml;

import com.mindalliance.channels.core.model.InfoFormat;
import com.mindalliance.channels.core.model.ModelEntity;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * Info format XML converter.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 10/30/12
 * Time: 1:50 PM
 */
public class InfoFormatConverter extends EntityConverter {

    public InfoFormatConverter( XmlStreamer.Context context ) {
        super( context );
    }

    @Override
    public boolean canConvert( Class type ) {
        return InfoFormat.class.isAssignableFrom( type );
    }

    @Override
    protected Class<? extends ModelEntity> getEntityClass() {
        return InfoFormat.class;
    }


    @Override
    protected void setSpecific( ModelEntity entity, String nodeName, HierarchicalStreamReader reader, UnmarshallingContext context ) {
        // Do nothing
    }

    @Override
    protected void writeSpecifics( ModelEntity entity, HierarchicalStreamWriter writer, MarshallingContext context ) {
        // Do nothing
    }
}
