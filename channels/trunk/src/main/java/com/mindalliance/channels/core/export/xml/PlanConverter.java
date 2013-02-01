/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.core.export.xml;

import com.mindalliance.channels.core.dao.PlanDao;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Classification;
import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Phase;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.model.Requirement;
import com.mindalliance.channels.core.model.Role;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.model.TransmissionMedium;
import com.mindalliance.channels.core.model.UserIssue;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Plan XML converter.
 */
public class PlanConverter extends AbstractChannelsConverter {

    /**
     * Class logger.
     */
    public static final Logger LOG = LoggerFactory.getLogger( PlanConverter.class );

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat( "yyyy/MM/dd H:mm:ss z" );

    public PlanConverter( XmlStreamer.Context context ) {
        super( context );
    }

    @Override
    public boolean canConvert( Class aClass ) {
        return Plan.class.isAssignableFrom( aClass );
    }

    @Override
    public void marshal( Object source, HierarchicalStreamWriter writer, MarshallingContext context ) {
        Plan plan = (Plan) source;
        PlanDao planDao = getPlanDao();
        writer.addAttribute( "id", String.valueOf( plan.getId() ) );
        writer.addAttribute( "uri", plan.getUri() );
        writer.addAttribute( "version", getVersion() );
        writer.addAttribute( "date", DATE_FORMAT.format( new Date() ) );
        writer.startNode( "whenVersioned" );
        writer.setValue( DATE_FORMAT.format( plan.getWhenVersioned() ) );
        writer.endNode();
        writer.startNode( "lastId" );
        writer.setValue( String.valueOf( planDao.getIdGenerator().getIdCounter( getContext().getPlan().getUri() ) ) );
        for ( Date date : plan.getIdShifts().keySet() ) {
            writer.startNode( "idShift" );
            writer.addAttribute( "date", DATE_FORMAT.format( date ) );
            writer.addAttribute( "shift", Long.toString( plan.getIdShifts().get( date ) ) );
            writer.endNode();
        }
        writer.endNode();
        writer.startNode( "name" );
        writer.setValue( plan.getName() );
        writer.endNode();
        writer.startNode( "client" );
        writer.setValue( plan.getClient() );
        writer.endNode();
        writer.startNode( "description" );
        writer.setValue( plan.getDescription() );
        writer.endNode();
        writer.startNode( "plannerSupportCommunity" );
        writer.setValue( plan.getPlannerSupportCommunity() );
        writer.endNode();
        writer.startNode( "userSupportCommunity" );
        writer.setValue( plan.getUserSupportCommunity() );
        writer.endNode();
        writer.startNode( "communityCalendarHost" );
        writer.setValue( plan.getCommunityCalendarHost() );
        writer.endNode();
        writer.startNode( "communityCalendar" );
        writer.setValue( plan.getCommunityCalendar() );
        writer.endNode();
        writer.startNode( "communityCalendarPrivateTicket" );
        writer.setValue( plan.getCommunityCalendarPrivateTicket() );
        writer.endNode();
        writer.startNode( "surveyApiKey" );
        writer.setValue( plan.getSurveyApiKey() );
        writer.endNode();
        writer.startNode( "surveyTemplate" );
        writer.setValue( plan.getSurveyTemplate() );
        writer.endNode();
        writer.startNode( "surveyUserKey" );
        writer.setValue( plan.getSurveyUserKey() );
        writer.endNode();
        writer.startNode( "surveyDefaultEmailAddress" );
        writer.setValue( plan.getSurveyDefaultEmailAddress() );
        writer.endNode();
        writer.startNode( "viewableByAll" );
        writer.setValue( Boolean.toString( plan.isViewableByAll() ) );
        writer.endNode();
        writer.startNode( "defaultLanguage" );
        writer.setValue( plan.getDefaultLanguage() );
        writer.endNode();

        // Producers - planners who voted to put this version into production
        for ( String producer : plan.getProducers() ) {
            writer.startNode( "producer" );
            writer.setValue( producer );
            writer.endNode();
        }

        // Classifications
        for ( Classification classification : plan.getClassifications() ) {
            writer.startNode( "classification" );
            context.convertAnother( classification );
            writer.endNode();
        }
        exportDetectionWaivers( plan, writer );
        exportAttachments( plan, writer );
        context.put( "exporting-plan", "true" );

        // All entities
        Iterator<ModelEntity> entities = planDao.iterateEntities();
        while ( entities.hasNext() ) {
            ModelEntity entity = entities.next();
            if ( !entity.isImmutable() ) {
                writer.startNode( entity.getTypeName().toLowerCase() );
                context.convertAnother( entity );
                writer.endNode();
            }
        }

        // All incidents
        for ( Event event : plan.getIncidents() ) {
            writer.startNode( "incident" );
            writer.addAttribute( "id", Long.toString( event.getId() ) );
            writer.setValue( event.getName() );
            writer.endNode();
        }

        // All phases
        for ( Phase phase : plan.getPhases() ) {
            writer.startNode( "plan-phase" );
            writer.addAttribute( "id", Long.toString( phase.getId() ) );
            writer.setValue( phase.getName() );
            writer.endNode();
        }

        // All organizations to be involved
        for ( Organization organization : plan.getOrganizations() ) {
            writer.startNode( "organization-involved" );
            writer.addAttribute( "id", Long.toString( organization.getId() ) );
            writer.setValue( organization.getName() );
            writer.endNode();
        }

        // All segments
        for ( Segment segment : plan.getSegments() ) {
            writer.startNode( "segment" );
            context.convertAnother( segment, new SegmentConverter( getContext() ) );
            writer.endNode();
        }

        // All assignment requirements
        for ( Requirement requirement : planDao.list( Requirement.class ) ) {
            if ( !requirement.isUnknown() ) {
                writer.startNode( "requirement" );
                context.convertAnother( requirement );
                writer.endNode();
            }
        }
        // Export user issues
        exportUserIssues( plan, writer, context );
        Place locale = plan.getLocale();
        if ( locale != null && !locale.getName().trim().isEmpty() ) {
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
        context.put( "importing-plan", "true" );
        Plan plan = getContext().getPlan();
        String uri = reader.getAttribute( "uri" );
        plan.setUri( uri );
        Long id = Long.parseLong( reader.getAttribute( "id" ) );
        plan.setId( id );
        getPlanDao().loadingModelContextWithId( id ); // can set a shift in all ids to prevent overshadowing of IDs in subDaos
        while ( reader.hasMoreChildren() ) {
            reader.moveDown();
            String nodeName = reader.getNodeName();
            if ( nodeName.equals( "lastId" ) ) {
                LOG.info( "Plan last saved with last id " + reader.getValue() );
            } else if ( nodeName.equals( "name" ) ) {
                plan.setName( reader.getValue() );
            } else if ( nodeName.equals( "defaultLanguage" ) ) {
                plan.setDefaultLanguage( reader.getValue() );
            } else if ( nodeName.equals( "template" ) || nodeName.equals( "viewableByAll" ) ) { // todo - "template" is deprecated
                plan.setViewableByAll( reader.getValue().equals( "true" ) );
            } else if ( nodeName.equals( "whenVersioned" ) ) {
                try {
                    Date whenVersion = DATE_FORMAT.parse( reader.getValue() );
                    plan.setWhenVersioned( whenVersion );
                } catch ( ParseException e ) {
                    throw new RuntimeException( e );
                }
            } else if ( nodeName.equals( "idShift" ) ) {
                try {
                    Date date = DATE_FORMAT.parse( reader.getAttribute( "date" ) );
                    Long shift = Long.parseLong( reader.getAttribute( "shift" ) );
                    plan.getIdShifts().put( date, shift );
                } catch ( ParseException e ) {
                    throw new RuntimeException( e );
                }
            } else if ( nodeName.equals( "client" ) ) {
                plan.setClient( reader.getValue() );
            } else if ( nodeName.equals( "plannerSupportCommunity" ) ) {
                plan.setPlannerSupportCommunity( reader.getValue() );
            } else if ( nodeName.equals( "userSupportCommunity" ) ) {
                plan.setUserSupportCommunity( reader.getValue() );
            } else if ( nodeName.equals( "communityCalendarHost" ) ) {
                plan.setCommunityCalendarHost( reader.getValue() );
            } else if ( nodeName.equals( "communityCalendar" ) ) {
                plan.setCommunityCalendar( reader.getValue() );
            } else if ( nodeName.equals( "communityCalendarPrivateTicket" ) ) {
                plan.setCommunityCalendarPrivateTicket( reader.getValue() );
            } else if ( nodeName.equals( "surveyApiKey" ) ) {
                plan.setSurveyApiKey( reader.getValue() );
            } else if ( nodeName.equals( "surveyUserKey" ) ) {
                plan.setSurveyUserKey( reader.getValue() );
            } else if ( nodeName.equals( "surveyTemplate" ) ) {
                plan.setSurveyTemplate( reader.getValue() );
            } else if ( nodeName.equals( "surveyDefaultEmailAddress" ) ) {
                plan.setSurveyDefaultEmailAddress( reader.getValue() );
            } else if ( nodeName.equals( "description" ) ) {
                plan.setDescription( reader.getValue() );
                // Entities
            } else if ( nodeName.equals( "actor" ) ) {
                context.convertAnother( plan, Actor.class );
            } else if ( nodeName.equals( "participation" ) ) {
                LOG.debug( "Obsolete element: participation" );
            } else if ( nodeName.equals( "organization" ) ) {
                context.convertAnother( plan, Organization.class );
            } else if ( nodeName.equals( "role" ) ) {
                context.convertAnother( plan, Role.class );
            } else if ( nodeName.equals( "place" ) ) {
                context.convertAnother( plan, Place.class );
            } else if ( nodeName.equals( "event" ) ) {
                context.convertAnother( plan, Event.class );
            } else if ( nodeName.equals( "classification" ) ) {
                // conversion adds classification to plan
                context.convertAnother( plan, Classification.class );
            } else if ( nodeName.equals( "phase" ) ) {
                context.convertAnother( plan, Phase.class );
            } else if ( nodeName.equals( "medium" ) ) {
                context.convertAnother( plan, TransmissionMedium.class );
            } else if ( nodeName.equals( "incident" ) ) {
                String eventId = reader.getAttribute( "id" );
                Event event = findOrCreateType( Event.class, reader.getValue(), eventId );
                plan.addIncident( event );
                // Phases
            } else if ( nodeName.equals( "plan-phase" ) ) {
                String phaseId = reader.getAttribute( "id" );
                String name = reader.getValue();
                Phase phase = findOrCreate( Phase.class, name, phaseId );
                plan.addPhase( phase );
                // Organizations involved
            } else if ( nodeName.equals( "organization-involved" ) ) {
                String orgId = reader.getAttribute( "id" );
                Organization organization = findOrCreate( Organization.class, reader.getValue(), orgId );
                plan.addOrganization( organization );
                // Producers
            } else if ( nodeName.equals( "producer" ) ) {
                plan.addProducer( reader.getValue() );
                // Segments
            } else if ( nodeName.equals( "segment" ) ) {
                context.convertAnother( plan, Segment.class );
            } else if ( nodeName.equals( "detection-waivers" ) ) {
                importDetectionWaivers( plan, reader );
            } else if ( nodeName.equals( "attachments" ) ) {
                importAttachments( plan, reader );
            } else if ( nodeName.equals( "issue" ) ) {
                context.convertAnother( plan, UserIssue.class );
            } else if ( nodeName.equals( "locale" ) ) {
                String placeId = reader.getAttribute( "id" );
                String kindName = reader.getAttribute( "kind" );
                String name = reader.getValue();
                Place locale = this.getEntity( Place.class,
                        name,
                        Long.getLong( placeId ),
                        ModelEntity.Kind.valueOf( kindName ),
                        context );
                plan.setLocale( locale );
            } else if ( nodeName.equals( "requirement" ) ) {
                // requirement always added to plan on creation by dao
                context.convertAnother(
                        plan,
                        Requirement.class );
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
