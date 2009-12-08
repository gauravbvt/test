package com.mindalliance.channels.export.xml;

import com.mindalliance.channels.model.Classification;
import com.mindalliance.channels.model.ModelEntity;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.model.TransmissionMedium;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Transmission mdeium converter.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Dec 7, 2009
 * Time: 10:33:54 AM
 */
public class TransmissionMediumConverter extends EntityConverter {

    /**
     * Class logger.
     */
    public static final Logger LOG = LoggerFactory.getLogger( TransmissionMediumConverter.class );

    public TransmissionMediumConverter( XmlStreamer.Context context ) {
        super( context );
    }

    /**
     * {@inheritDoc}
     */
    public boolean canConvert( Class type ) {
        return TransmissionMedium.class.isAssignableFrom( type );
    }

    /**
     * {@inheritDoc}
     */
    protected Class<? extends ModelEntity> getEntityClass() {
        return TransmissionMedium.class;
    }

    /**
     * {@inheritDoc}
     */
    protected void writeSpecifics( ModelEntity entity,
                                   HierarchicalStreamWriter writer,
                                   MarshallingContext context ) {
        TransmissionMedium medium = (TransmissionMedium) entity;
        writer.startNode( "addressPattern" );
        writer.setValue( medium.getAddressPattern() );
        writer.endNode();
        writer.startNode( "unicast" );
        writer.setValue( medium.isUnicast() ? "true" : "false" );
        writer.endNode();
        for ( Classification classification : medium.getSecurity() ) {
            writer.startNode( "secureFor" );
            context.convertAnother( classification );
            writer.endNode();
        }
    }

    /**
     * {@inheritDoc}
     */
    protected void setSpecific( ModelEntity entity,
                                String nodeName,
                                HierarchicalStreamReader reader,
                                UnmarshallingContext context ) {
        Plan plan = getPlan();
        TransmissionMedium medium = (TransmissionMedium) entity;
        if ( nodeName.equals( "addressPattern" ) ) {
            medium.setAddressPattern( reader.getValue() );
        } else if ( nodeName.equals( "unicast" ) ) {
            medium.setUnicast( reader.getValue().equals( "true" ) );
        } else if ( nodeName.equals( "secureFor" ) ) {
            Classification classification = (Classification) context.convertAnother(
                    plan,
                    ClassificationConverter.class );
            medium.addSecurity( classification );
        } else {
            LOG.warn( "Unknown element " + nodeName );
        }
    }

}
