package com.mindalliance.channels.core.export.xml;

import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Channel;
import com.mindalliance.channels.core.model.Job;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.CollaborationModel;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * XStream organization converter
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 16, 2009
 * Time: 6:53:05 PM
 */
public class OrganizationConverter extends EntityConverter {

    /**
     * Class logger.
     */
    public static final Logger LOG = LoggerFactory.getLogger( OrganizationConverter.class );


    public OrganizationConverter( XmlStreamer.Context context ) {
        super( context );
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
    protected Class<? extends ModelEntity> getEntityClass() {
        return Organization.class;
    }

    /**
     * {@inheritDoc}
     */
    protected void writeSpecifics( ModelEntity entity,
                                   HierarchicalStreamWriter writer,
                                   MarshallingContext context ) {
        Organization org = (Organization) entity;
        if ( org.isPlaceHolder() ) {
            writer.startNode( "placeHolder" );
            writer.setValue( "true" );
            writer.endNode();
         }
        if ( org.isSingleParticipation() ) {
            writer.startNode( "singleParticipation" );
            writer.setValue( "true" );
            writer.endNode();
        }
        Actor custodian = org.getCustodian();
        if ( custodian != null && !custodian.getName().trim().isEmpty() ) {
            writer.startNode( "custodian" );
            writer.addAttribute( "id", Long.toString( custodian.getId() ) );
            writer.setValue( custodian.getName() );
            writer.endNode();
        }
/*
        if ( org.isActorsRequired() ) {
            writer.startNode( "actorsRequired" );
            writer.setValue( "true" );
            writer.endNode();
        }
        if ( org.isAgreementsRequired() ) {
            writer.startNode( "agreementsRequired" );
            writer.setValue( "true" );
            writer.endNode();
        }
*/
        Organization parent = org.getParent();
        if ( parent != null && !parent.getName().trim().isEmpty() ) {
            writer.startNode( "parent" );
            writer.addAttribute( "id", Long.toString( parent.getId() ) );
            writer.addAttribute( "kind", parent.getKind().name() );
            writer.setValue( parent.getName() );
            writer.endNode();
        }
        Place location = org.getLocation();
        if ( location != null && !location.getName().trim().isEmpty() ) {
            writer.startNode( "location" );
            writer.addAttribute( "id", Long.toString( location.getId() ) );
            writer.setValue( location.getName() );
            writer.endNode();
        }
        // mission
        writer.startNode( "mission" );
        writer.setValue( org.getMission() );
        writer.endNode();
        // channels
        for ( Channel channel : org.getChannels() ) {
            writer.startNode( "channel" );
            context.convertAnother( channel );
            writer.endNode();
        }
        // jobs
        for ( Job job : org.getJobs() ) {
            writer.startNode( "job" );
            context.convertAnother( job );
            writer.endNode();
        }
        // agreements
/*
        for ( Agreement agreement : org.getAgreements()) {
            writer.startNode( "agreement" );
            context.convertAnother( agreement );
            writer.endNode();
        }
*/
        // not deployed transmission media
/*
        for ( TransmissionMedium medium : org.getMediaNotDeployed() ) {
            writer.startNode( "mediumNotDeployed" );
            writer.addAttribute( "id", Long.toString( medium.getId() ) );
            writer.setValue( medium.getName() );
            writer.endNode();
        }
*/
    }

    /**
     * {@inheritDoc}
     */
    protected void setSpecific( ModelEntity entity,
                                String nodeName,
                                HierarchicalStreamReader reader,
                                UnmarshallingContext context ) {
        Organization org = (Organization) entity;
        CollaborationModel collaborationModel = getPlan();
        if ( nodeName.equals( "placeHolder" ) ) {
            org.setPlaceHolder( reader.getValue().equals( "true" ) );
        } else if ( nodeName.equals( "singleParticipation" ) ) {
            org.setSingleParticipation( reader.getValue().equals( "true" ) );
        } else if ( nodeName.equals( "custodian" ) ) {
            String id = reader.getAttribute( "id");
            org.setCustodian( findOrCreate( Actor.class, reader.getValue(), id ) );
/*
        } else if ( nodeName.equals( "actorsRequired" ) ) {
            org.setActorsRequired( reader.getValue().equals( "true" ) );
        } else if ( nodeName.equals( "agreementsRequired" ) ) {
            org.setAgreementsRequired( reader.getValue().equals( "true" ) );
*/
        } else if ( nodeName.equals( "mission" ) ) {
            org.setMission( reader.getValue() );
        } else if ( nodeName.equals( "parent" ) ) {
            Long id = Long.parseLong( reader.getAttribute( "id") );
            String kindName = reader.getAttribute( "kind" );
            ModelEntity.Kind kind;
            if (kindName == null) {
                kind = ModelEntity.Kind.Actual;
            } else {
                kind = ModelEntity.Kind.valueOf( kindName );
            }
            Organization parent = this.getEntity(
                    Organization.class,
                    reader.getValue(),
                    id,
                    kind,
                    context
            );
            org.setParent( parent );
        } else if ( nodeName.equals( "location" ) ) {
            String id = reader.getAttribute( "id");
            org.setLocation( findOrCreate( Place.class, reader.getValue(), id ) );
        } else if ( nodeName.equals( "channel" ) ) {
            Channel channel = (Channel) context.convertAnother( collaborationModel, Channel.class );
            org.addChannel( channel );
        } else if ( nodeName.equals( "job" ) ) {
            Job job = (Job) context.convertAnother( collaborationModel, Job.class );
            org.addJob( job );
/*
        } else if ( nodeName.equals( "agreement" ) ) {   // todo - obsolete
            Agreement agreement = (Agreement) context.convertAnother( plan, Agreement.class );
            org.addAgreement( agreement );
        } else if ( nodeName.equals( "mediumNotDeployed" ) ) {
            String id = reader.getAttribute( "id");
            org.addMediumNotDeployed( findOrCreate( TransmissionMedium.class, reader.getValue(), id ) );
*/
        } else {
            LOG.debug( "Unknown element " + nodeName );
        }
    }
}
