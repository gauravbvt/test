package com.mindalliance.channels.export.xml;

import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Classification;
import com.mindalliance.channels.model.Event;
import com.mindalliance.channels.model.ModelEntity;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Phase;
import com.mindalliance.channels.model.Place;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.model.Role;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.model.TransmissionMedium;
import com.mindalliance.channels.model.User;
import com.mindalliance.channels.model.UserIssue;
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
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 22, 2009
 * Time: 12:05:43 PM
 */
public class PlanConverter extends AbstractChannelsConverter {

    /**
     * Class logger.
     */
    public static final Logger LOG = LoggerFactory.getLogger( PlanConverter.class );

    public PlanConverter( XmlStreamer.Context context ) {
        super( context );
    }

    /**
     * {@inheritDoc}
     */
    public boolean canConvert( Class aClass ) {
        return Plan.class.isAssignableFrom( aClass );
    }

    /**
     * {@inheritDoc}
     */
    public void marshal(
            Object obj,
            HierarchicalStreamWriter writer,
            MarshallingContext context ) {
        Plan plan = (Plan) obj;
        QueryService queryService = getQueryService();
        writer.addAttribute( "id", String.valueOf( plan.getId() ) );
        writer.addAttribute( "uri", plan.getUri() );
        writer.addAttribute( "version", getVersion() );
        writer.addAttribute( "date", new SimpleDateFormat( "yyyy/MM/dd H:mm:ss z" ).format( new Date() ) );
        writer.startNode( "whenVersioned" );
        writer.setValue( new SimpleDateFormat( "yyyy/MM/dd H:mm:ss z" ).format( plan.getWhenVersioned() ) );
        writer.endNode();
        writer.startNode( "lastId" );
        writer.setValue( String.valueOf(
                getContext().getIdGenerator().getLastAssignedId( getContext().getPlan() ) ) );
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
        Iterator<ModelEntity> entities = queryService.iterateEntities();
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
        // All segment
        for ( Segment segment : plan.getSegments() ) {
            writer.startNode( "segment" );
            context.convertAnother( segment, new SegmentConverter( getContext() ) );
            writer.endNode();
        }
        // Export plan issues
        exportUserIssues( plan, writer, context );
    }

    /**
     * {@inheritDoc}
     */
    public Object unmarshal(
            HierarchicalStreamReader reader,
            UnmarshallingContext context ) {
        getProxyConnectors( context );
        context.put( "importing-plan", "true" );
        Plan plan = getContext().getPlan();
        User.current().setPlan( plan );
        String uri = reader.getAttribute( "uri" );
        plan.setUri( uri );
        Long id = Long.parseLong( reader.getAttribute( "id" ) );
        plan.setId( id );
        while ( reader.hasMoreChildren() ) {
            reader.moveDown();
            String nodeName = reader.getNodeName();
            if ( nodeName.equals( "lastId" ) ) {
                LOG.info( "Plan last saved with last id " + reader.getValue() );
            } else if ( nodeName.equals( "name" ) ) {
                plan.setName( reader.getValue() );
            } else if ( nodeName.equals( "whenVersioned" ) ) {
                try {
                    Date whenVersion = new SimpleDateFormat( "yyyy/MM/dd H:mm:ss z" ).parse( reader.getValue() );
                    plan.setWhenVersioned( whenVersion );
                } catch ( ParseException e ) {
                    throw new RuntimeException( e );
                }
            } else if ( nodeName.equals( "client" ) ) {
                plan.setClient( reader.getValue() );
            } else if ( nodeName.equals( "description" ) ) {
                plan.setDescription( reader.getValue() );
                // Entities
            } else if ( nodeName.equals( "actor" ) ) {
                context.convertAnother( plan, Actor.class );
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
            }  else if ( nodeName.equals( "medium" ) ) {
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
            }  else if ( nodeName.equals( "organization-involved" ) ) {
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
            } else {
                LOG.warn( "Unknown element " + nodeName );
            }
            reader.moveUp();
        }
        Map<String, Object> state = new HashMap<String, Object>();
        state.put( "idMap", context.get( "idMap" ) );
        state.put( "proxyConnectors", context.get( "proxyConnectors" ) );
        return state;
    }

}
