package com.mindalliance.channels.engine.export.xml;

import com.mindalliance.channels.core.model.Classification;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.model.TransmissionMedium;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Transmission medium converter.
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
        for ( Classification classification : medium.getSecurity() ) {
            writer.startNode( "secureForClassification" );
            context.convertAnother( classification );
            writer.endNode();
        }
        if ( medium.getReach() != null ) {
            Place reach = medium.getReach();
            writer.startNode( "reach" );
            writer.addAttribute( "id", Long.toString( reach.getId() ) );
            writer.addAttribute( "kind", reach.isType() ? "Type" : "Actual" );
            writer.setValue( reach.getName() );
            writer.endNode();
        }
        for ( TransmissionMedium delegatedTo : medium.getDelegatedToMedia() ) {
            writer.startNode( "delegatesTo" );
            writer.addAttribute( "id", Long.toString( delegatedTo.getId() ) );
            writer.addAttribute( "kind", "Type" );
            writer.setValue( delegatedTo.getName() );
            writer.endNode();
        }
        if ( medium.getCast() != null ) {
            writer.startNode( "cast" );
            writer.setValue( medium.getCast().name() );
            writer.endNode();
        }
        // Synchrone
        writer.startNode( "synchronous" );
        writer.setValue( Boolean.toString( medium.isSynchronous() ) );
        writer.endNode();
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
        } else if ( nodeName.equals( "cast" ) ) {
            medium.setCast( TransmissionMedium.Cast.valueOf( reader.getValue() ) );
        } else if ( nodeName.equals( "secureForClassification" ) ) {
            Classification classification = (Classification) context.convertAnother(
                    plan,
                    Classification.class );
            medium.addSecurity( classification );
        } else if ( nodeName.equals( "reach" ) ) {
            String idString = reader.getAttribute( "id" );
            ModelEntity.Kind kind = kind( reader.getAttribute( "kind" ) );
            medium.setReach( getEntity(
                    Place.class,
                    reader.getValue(),
                    Long.parseLong( idString ),
                    kind,
                    context ) );

        } else if ( nodeName.equals( "delegatesTo" ) ) {
            String idString = reader.getAttribute( "id" );
            ModelEntity.Kind kind = kind( reader.getAttribute( "kind" ) );
            TransmissionMedium delegatedTo = getEntity(
                    TransmissionMedium.class,
                    reader.getValue(),
                    Long.parseLong( idString ),
                    kind, context );
            medium.addDelegatedToMedium( delegatedTo );
        } else if ( nodeName.equals(  "synchronous" )) {
            medium.setSynchronous( reader.getValue().equals( "true" ) );
        } else {
            LOG.warn( "Unknown element " + nodeName );
        }
    }

}
