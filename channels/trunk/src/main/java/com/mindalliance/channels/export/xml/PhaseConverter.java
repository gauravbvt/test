package com.mindalliance.channels.export.xml;

import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Phase;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * Phase XML converter.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Sep 18, 2009
 * Time: 12:03:08 PM
 */
public class PhaseConverter extends EntityConverter {

    public PhaseConverter( XmlStreamer.Context context ) {
        super( context );
    }

    /**
     * {@inheritDoc}
     */
    public boolean canConvert( Class aClass ) {
        return Phase.class.isAssignableFrom( aClass );
    }

    /**
     * {@inheritDoc}
     */
    ModelObject findOrMakeEntity( String name, Long id, boolean importingPlan ) {
        return importingPlan
                ? getQueryService().findOrCreate( Phase.class, name, id )
                : getQueryService().findOrCreate( Phase.class, name );
    }

    /**
     * {@inheritDoc}
     */
    protected void writeSpecifics( ModelObject entity,
                                   HierarchicalStreamWriter writer,
                                   MarshallingContext context ) {
        Phase phase = (Phase) entity;
        Phase.Timing timing = phase.getTiming();
        writer.startNode( "timing" );
        writer.setValue( timing.name() );
        writer.endNode();
    }

    /**
     * {@inheritDoc}
     */
    protected void setSpecific(
            ModelObject entity,
            String nodeName,
            HierarchicalStreamReader reader,
            UnmarshallingContext context ) {
        Phase phase = (Phase) entity;
        if ( nodeName.equals( "timing" ) ) {
            phase.setTiming( Phase.Timing.valueOf( reader.getValue() ));
        }
    }

}
