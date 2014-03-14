/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.core.export.xml;

import com.mindalliance.channels.core.dao.ModelDao;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Classification;
import com.mindalliance.channels.core.model.CollaborationModel;
import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.Function;
import com.mindalliance.channels.core.model.InfoFormat;
import com.mindalliance.channels.core.model.InfoProduct;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Phase;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.Requirement;
import com.mindalliance.channels.core.model.Role;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.model.TransmissionMedium;
import com.mindalliance.channels.core.model.UserIssue;
import com.mindalliance.channels.core.model.asset.MaterialAsset;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Plan XML converter.
 */
public class ModelConverter extends AbstractChannelsConverter {

    /**
     * Class logger.
     */
    public static final Logger LOG = LoggerFactory.getLogger( ModelConverter.class );


    public ModelConverter( XmlStreamer.Context context ) {
        super( context );
    }

    @Override
    public boolean canConvert( Class aClass ) {
        return CollaborationModel.class.isAssignableFrom( aClass );
    }

    @Override
    public void marshal( Object source, HierarchicalStreamWriter writer, MarshallingContext context ) {
        CollaborationModel collaborationModel = (CollaborationModel) source;
        ModelDao modelDao = getModelDao();
        writer.addAttribute( "id", String.valueOf( collaborationModel.getId() ) );
        writer.addAttribute( "uri", collaborationModel.getUri() );
        writer.addAttribute( "version", getVersion() );
        writer.addAttribute( "date", getDateFormat().format( new Date() ) );
        writer.startNode( "whenVersioned" );
        writer.setValue( getDateFormat().format( collaborationModel.getWhenVersioned() ) );
        writer.endNode();
        writer.startNode( "lastId" );
        writer.setValue( String.valueOf( modelDao.getIdGenerator().getIdCounter( getContext().getPlan().getUri() ) ) );
        for ( Date date : collaborationModel.getIdShifts().keySet() ) {
            writer.startNode( "idShift" );
            writer.addAttribute( "date", getDateFormat().format( date ) );
            writer.addAttribute( "shift", Long.toString( collaborationModel.getIdShifts().get( date ) ) );
            writer.endNode();
        }
        writer.endNode();
        writer.startNode( "name" );
        writer.setValue( collaborationModel.getName() );
        writer.endNode();
        writer.startNode( "client" );
        writer.setValue( collaborationModel.getClient() );
        writer.endNode();
        writer.startNode( "description" );
        writer.setValue( collaborationModel.getDescription() );
        writer.endNode();
        writer.startNode( "plannerSupportCommunity" );
        writer.setValue( collaborationModel.getPlannerSupportCommunity() );
        writer.endNode();
        writer.startNode( "userSupportCommunity" );
        writer.setValue( collaborationModel.getUserSupportCommunity() );
        writer.endNode();
        writer.startNode( "communityCalendarHost" );
        writer.setValue( collaborationModel.getCommunityCalendarHost() );
        writer.endNode();
        writer.startNode( "communityCalendar" );
        writer.setValue( collaborationModel.getCommunityCalendar() );
        writer.endNode();
        writer.startNode( "communityCalendarPrivateTicket" );
        writer.setValue( collaborationModel.getCommunityCalendarPrivateTicket() );
        writer.endNode();
        writer.startNode( "viewableByAll" );
        writer.setValue( Boolean.toString( collaborationModel.isViewableByAll() ) );
        writer.endNode();
        writer.startNode( "defaultLanguage" );
        writer.setValue( collaborationModel.getDefaultLanguage() );
        writer.endNode();

        // Producers - developers who voted to put this version into production
        for ( String producer : collaborationModel.getProducers() ) {
            writer.startNode( "producer" );
            writer.setValue( producer );
            writer.endNode();
        }

        // Classifications
        for ( Classification classification : collaborationModel.getClassifications() ) {
            writer.startNode( "classification" );
            context.convertAnother( classification );
            writer.endNode();
        }
        exportDetectionWaivers( collaborationModel, writer );
        exportAttachments( collaborationModel, writer );
        context.put( "exporting-model", "true" );

        // All entities
        Iterator<ModelEntity> entities = modelDao.iterateEntities();
        while ( entities.hasNext() ) {
            ModelEntity entity = entities.next();
            if ( !entity.isImmutable() ) {
                writer.startNode( entity.getTypeName().toLowerCase() );
                context.convertAnother( entity );
                writer.endNode();
            }
        }

        // All incidents
        for ( Event event : collaborationModel.getIncidents() ) {
            writer.startNode( "incident" );
            writer.addAttribute( "id", Long.toString( event.getId() ) );
            writer.setValue( event.getName() );
            writer.endNode();
        }

        // All phases
        for ( Phase phase : collaborationModel.getPhases() ) {
            writer.startNode( "plan-phase" );
            writer.addAttribute( "id", Long.toString( phase.getId() ) );
            writer.setValue( phase.getName() );
            writer.endNode();
        }

        // All entities to be involved
        writer.startNode( "entities-involved" );
        for ( ModelEntity involvedEntity : collaborationModel.getInvolvements() ) {
            writer.startNode( involvedEntity.getTypeName().toLowerCase() );
            context.convertAnother( involvedEntity );
            writer.endNode();
        }
        writer.endNode();

        // All segments
        for ( Segment segment : collaborationModel.getSegments() ) {
            writer.startNode( "segment" );
            context.convertAnother( segment, new SegmentConverter( getContext() ) );
            writer.endNode();
        }

        // All assignment requirements   -- TODO - OBSOLETE
        for ( Requirement requirement : modelDao.list( Requirement.class ) ) {
            if ( !requirement.isUnknown() ) {
                writer.startNode( "requirement" );
                context.convertAnother( requirement );
                writer.endNode();
            }
        }
        // Export user issues
        exportUserIssues( collaborationModel, writer, context );
        Place locale = collaborationModel.getLocale();
        if ( locale != null && !locale.getName().trim().isEmpty() && locale.isActual() ) {
            writer.startNode( "locale" );
            writer.addAttribute( "id", Long.toString( locale.getId() ) );
            writer.addAttribute( "kind", locale.getKind().name() );
            writer.setValue( locale.getName() );
            writer.endNode();
        }
    }

    @Override
    public Object unmarshal( HierarchicalStreamReader reader, UnmarshallingContext context ) {
        getProxyConnectors( context );
        context.put( "importing-model", "true" );
        CollaborationModel collaborationModel = getContext().getPlan();
        String uri = reader.getAttribute( "uri" );
        collaborationModel.setUri( uri );
        Long id = Long.parseLong( reader.getAttribute( "id" ) );
        collaborationModel.setId( id );
        getModelDao().loadingModelContextWithId( id ); // can set a shift in all ids to prevent overshadowing of IDs in subDaos
        while ( reader.hasMoreChildren() ) {
            reader.moveDown();
            String nodeName = reader.getNodeName();
            if ( nodeName.equals( "lastId" ) ) {
                LOG.info( "Model last saved with last id " + reader.getValue() );
            } else if ( nodeName.equals( "name" ) ) {
                collaborationModel.setName( reader.getValue() );
            } else if ( nodeName.equals( "defaultLanguage" ) ) {
                collaborationModel.setDefaultLanguage( reader.getValue() );
            } else if ( nodeName.equals( "template" ) || nodeName.equals( "viewableByAll" ) ) { // todo - "template" is deprecated
                collaborationModel.setViewableByAll( reader.getValue().equals( "true" ) );
            } else if ( nodeName.equals( "whenVersioned" ) ) {
                try {
                    Date whenVersion = getDateFormat().parse( reader.getValue() );
                    collaborationModel.setWhenVersioned( whenVersion );
                } catch ( ParseException e ) {
                    throw new RuntimeException( e );
                }
            } else if ( nodeName.equals( "idShift" ) ) {
                try {
                    Date date = getDateFormat().parse( reader.getAttribute( "date" ) );
                    Long shift = Long.parseLong( reader.getAttribute( "shift" ) );
                    collaborationModel.getIdShifts().put( date, shift );
                } catch ( ParseException e ) {
                    throw new RuntimeException( e );
                }
            } else if ( nodeName.equals( "client" ) ) {
                collaborationModel.setClient( reader.getValue() );
            } else if ( nodeName.equals( "plannerSupportCommunity" ) ) {
                collaborationModel.setPlannerSupportCommunity( reader.getValue() );
            } else if ( nodeName.equals( "userSupportCommunity" ) ) {
                collaborationModel.setUserSupportCommunity( reader.getValue() );
            } else if ( nodeName.equals( "communityCalendarHost" ) ) {
                collaborationModel.setCommunityCalendarHost( reader.getValue() );
            } else if ( nodeName.equals( "communityCalendar" ) ) {
                collaborationModel.setCommunityCalendar( reader.getValue() );
            } else if ( nodeName.equals( "communityCalendarPrivateTicket" ) ) {
                collaborationModel.setCommunityCalendarPrivateTicket( reader.getValue() );
            } else if ( nodeName.equals( "surveyApiKey" ) ) {
                // do nothing - obsolete
            } else if ( nodeName.equals( "surveyUserKey" ) ) {
                // do nothing - obsolete
            } else if ( nodeName.equals( "surveyTemplate" ) ) {
                // do nothing - obsolete
            } else if ( nodeName.equals( "surveyDefaultEmailAddress" ) ) {
                // do nothing - obsolete
            } else if ( nodeName.equals( "description" ) ) {
                collaborationModel.setDescription( reader.getValue() );
                // Entities
            } else if ( nodeName.equals( "actor" ) ) {
                context.convertAnother( collaborationModel, Actor.class );
            } else if ( nodeName.equals( "participation" ) ) {
                LOG.debug( "Obsolete element: participation" );
            } else if ( nodeName.equals( "organization" ) ) {
                context.convertAnother( collaborationModel, Organization.class );
            } else if ( nodeName.equals( "role" ) ) {
                context.convertAnother( collaborationModel, Role.class );
            } else if ( nodeName.equals( "place" ) ) {
                context.convertAnother( collaborationModel, Place.class );
            } else if ( nodeName.equals( "event" ) ) {
                context.convertAnother( collaborationModel, Event.class );
            } else if ( nodeName.equals( "classification" ) ) {
                // conversion adds classification to plan
                context.convertAnother( collaborationModel, Classification.class );
            } else if ( nodeName.equals( "phase" ) ) {
                context.convertAnother( collaborationModel, Phase.class );
            } else if ( nodeName.equals( "medium" ) ) {
                context.convertAnother( collaborationModel, TransmissionMedium.class );
            } else if ( nodeName.equals( "infoproduct" ) ) {
                context.convertAnother( collaborationModel, InfoProduct.class );
            } else if ( nodeName.equals( "format" ) ) {
                context.convertAnother( collaborationModel, InfoFormat.class );
            } else if ( nodeName.equals( "function" ) ) {
                context.convertAnother( collaborationModel, Function.class );
            } else if ( nodeName.equals( "asset" ) ) {
                context.convertAnother( collaborationModel, MaterialAsset.class );
            } else if ( nodeName.equals( "incident" ) ) {
                String eventId = reader.getAttribute( "id" );
                Event event = findOrCreateType( Event.class, reader.getValue(), eventId );
                collaborationModel.addIncident( event );
                // Phases
            } else if ( nodeName.equals( "plan-phase" ) ) {
                String phaseId = reader.getAttribute( "id" );
                String name = reader.getValue();
                Phase phase = findOrCreate( Phase.class, name, phaseId );
                collaborationModel.addPhase( phase );
                // Organizations involved
            } else if ( nodeName.equals( "organization-involved" ) ) { //OBSOLETE
                String orgId = reader.getAttribute( "id" );
                Organization organization = findOrCreate( Organization.class, reader.getValue(), orgId );
                collaborationModel.addEntityInvolved( organization );
            } else if ( nodeName.equals( "entities-involved" ) ) {
                while ( reader.hasMoreChildren() ) {
                    reader.moveDown();
                    String involvedName = reader.getNodeName();
                    if ( involvedName.equals( "organization" ) ) {
                        collaborationModel.addEntityInvolved( (ModelEntity) context.convertAnother( collaborationModel, Organization.class ) );
                    } else if ( involvedName.equals( "role" ) ) {
                        collaborationModel.addEntityInvolved( (ModelEntity) context.convertAnother( collaborationModel, Role.class ) );
                    } else if ( involvedName.equals( "place" ) ) {
                        collaborationModel.addEntityInvolved( (ModelEntity) context.convertAnother( collaborationModel, Place.class ) );
                    } else if ( nodeName.equals( "medium" ) ) {
                        collaborationModel.addEntityInvolved( (ModelEntity) context.convertAnother( collaborationModel, TransmissionMedium.class ) );
                    } else if ( nodeName.equals( "infoproduct" ) ) {
                        collaborationModel.addEntityInvolved( (ModelEntity) context.convertAnother( collaborationModel, InfoProduct.class ) );
                    } else if ( nodeName.equals( "format" ) ) {
                        collaborationModel.addEntityInvolved( (ModelEntity) context.convertAnother( collaborationModel, InfoFormat.class ) );
                    } else if ( nodeName.equals( "function" ) ) {
                        collaborationModel.addEntityInvolved( (ModelEntity) context.convertAnother( collaborationModel, Function.class ) );
                    }
                    reader.moveUp();
                }
            } else if ( nodeName.equals( "producer" ) ) {
                collaborationModel.addProducer( reader.getValue() );
                // Segments
            } else if ( nodeName.equals( "segment" ) ) {
                context.convertAnother( collaborationModel, Segment.class );
            } else if ( nodeName.equals( "detection-waivers" ) ) {
                importDetectionWaivers( collaborationModel, reader );
            } else if ( nodeName.equals( "attachments" ) ) {
                importAttachments( collaborationModel, reader );
            } else if ( nodeName.equals( "issue" ) ) {
                context.convertAnother( collaborationModel, UserIssue.class );
            } else if ( nodeName.equals( "locale" ) ) {
                String placeId = reader.getAttribute( "id" );
                String kindName = reader.getAttribute( "kind" );
                String name = reader.getValue();
                Place locale = this.getEntity( Place.class,
                        name,
                        Long.getLong( placeId ),
                        ModelEntity.Kind.valueOf( kindName ),
                        context );
                if ( locale != null && locale.isActual() )
                    collaborationModel.setLocale( locale );
            } else {
                LOG.debug( "Unknown element " + nodeName );
            }
            reader.moveUp();
        }
        Map<String, Object> state = new HashMap<String, Object>();
        state.put( "idMap", context.get( "idMap" ) );
        state.put( "proxyConnectors", context.get( "proxyConnectors" ) );
        return state;
    }
}
