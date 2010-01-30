package com.mindalliance.channels.export.xml;

import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Event;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.ModelEntity;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Phase;
import com.mindalliance.channels.model.Place;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.model.Risk;
import com.mindalliance.channels.model.Role;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.model.UserIssue;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * XStream segment converter.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Dec 16, 2008
 * Time: 1:47:49 PM
 */
public class SegmentConverter extends AbstractChannelsConverter {

    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( SegmentConverter.class );

    public SegmentConverter( XmlStreamer.Context context ) {
        super( context );
    }

    /**
     * {@inheritDoc}
     */
    public boolean canConvert( Class aClass ) {
        return Segment.class.isAssignableFrom( aClass );
    }

    /**
     * {@inheritDoc}
     */
    public void marshal( Object object, HierarchicalStreamWriter writer,
                         MarshallingContext context ) {
        Segment segment = (Segment) object;
        Plan plan = getContext().getPlan();
        QueryService queryService = getQueryService();
        boolean exportingInPlan = isExportingPlan( context ) || segment.isBeingDeleted();
        context.put( "segment", segment );
        writer.addAttribute( "plan", plan.getUri() );
        writer.addAttribute( "version", getVersion() );
        writer.addAttribute( "date", new SimpleDateFormat( "yyyy/MM/dd H:mm:ss z" ).format( new Date() ) );
        writer.addAttribute( "id", String.valueOf( segment.getId() ) );
        writer.addAttribute( "name", segment.getName() );
        writer.startNode( "description" );
        writer.setValue( segment.getDescription() );
        writer.endNode();
        exportDetectionWaivers( segment, writer );
        exportAttachments( segment, writer );
        if ( !exportingInPlan ) {
            // All entities if not within a plan export
            Iterator<ModelEntity> entities = queryService.iterateEntities();
            while ( entities.hasNext() ) {
                ModelEntity entity = entities.next();
                writer.startNode( entity.getClass().getSimpleName().toLowerCase() );
                context.convertAnother( entity );
                writer.endNode();
            }
            for ( Event incident : plan.getIncidents() ) {
                writer.startNode( "incident" );
                writer.addAttribute( "id", Long.toString( incident.getId() ) );
                writer.setValue( incident.getName() );
                writer.endNode();
            }
        }
        // Trigger event
        if ( segment.getEvent() != null ) {
            Event event = segment.getEvent();
            writer.startNode( "trigger-event" );
            writer.addAttribute( "id", Long.toString( event.getId() ) );
            writer.setValue( event.getName() );
            writer.endNode();
        }
        // Plan phase
        Phase phase = segment.getPhase();
        if ( phase == null ) {
            // Make sur a segment is always saved with a phase.
            phase = plan.getDefaultPhase( getQueryService() );
        }
        writer.startNode( "phase" );
        writer.addAttribute( "id", Long.toString( phase.getId() ) );
        writer.setValue( phase.getName() );
        writer.endNode();

        // Segment user issues
        exportUserIssues( segment, writer, context );
        // Risks in scope
        for ( Risk risk : segment.getRisks() ) {
            writer.startNode( "risk" );
            context.convertAnother( risk );
            writer.endNode();
        }
        // Parts
        Iterator<Part> parts = segment.parts();
        while ( parts.hasNext() ) {
            writer.startNode( "part" );
            context.convertAnother( parts.next() );
            writer.endNode();
        }
        // Flows
        Iterator<Flow> flows = segment.flows();
        while ( flows.hasNext() ) {
            writer.startNode( "flow" );
            Flow flow = flows.next();
            writer.addAttribute( "id", String.valueOf( flow.getId() ) );
            writer.addAttribute( "name", flow.getName() );
            context.convertAnother( flow );
            writer.endNode();
        }
    }


    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( "unchecked" )
    public Object unmarshal( HierarchicalStreamReader reader, UnmarshallingContext context ) {
        Map<Long, Long> idMap = getIdMap( context );
        boolean importingPlan = isImportingPlan( context );
        getProxyConnectors( context );
        QueryService queryService = getQueryService();
        Long oldId = Long.parseLong( reader.getAttribute( "id" ) );
        Segment segment = importingPlan
                ? queryService.createSegment( oldId )
                : queryService.createSegment();
        Part defaultPart = segment.getDefaultPart();
        context.put( "segment", segment );
        segment.setName( reader.getAttribute( "name" ) );
        idMap.put( oldId, segment.getId() );
        while ( reader.hasMoreChildren() ) {
            reader.moveDown();
            String nodeName = reader.getNodeName();
            if ( nodeName.equals( "description" ) ) {
                segment.setDescription( reader.getValue() );
            } else if ( nodeName.equals( "detection-waivers" ) ) {
                importDetectionWaivers( segment, reader );
            } else if ( nodeName.equals( "attachments" ) ) {
                importAttachments( segment, reader );
            } else if ( nodeName.equals( "event" ) ) {
                context.convertAnother( segment, Event.class );
            } else if ( nodeName.equals( "actor" ) ) {
                context.convertAnother( segment, Actor.class );
            } else if ( nodeName.equals( "organization" ) ) {
                context.convertAnother( segment, Organization.class );
            } else if ( nodeName.equals( "role" ) ) {
                context.convertAnother( segment, Role.class );
            } else if ( nodeName.equals( "place" ) ) {
                context.convertAnother( segment, Place.class );
                // Incident
            } else if ( nodeName.equals( "incident" ) ) {
                String id = reader.getAttribute( "id" );
                Event event = getEntity(
                        Event.class,
                        reader.getValue(),
                        Long.parseLong( id ),
                        true,
                        importingPlan,
                        idMap );
                getContext().getPlan().addIncident( event );
                // Event (always a type)
            } else if ( nodeName.equals( "trigger-event" ) ) {
                String id = reader.getAttribute( "id" );
                Event event = findOrCreateType( Event.class, reader.getValue(), id );
                segment.setEvent( event );
                // Phase
            } else if ( nodeName.equals( "phase" ) ) {
                String id = reader.getAttribute( "id" );
                Phase phase = findOrCreate( Phase.class, reader.getValue(), id );
                segment.setPhase( phase );
                // Parts and flows
            } else if ( nodeName.equals( "part" ) ) {
                context.convertAnother( segment, Part.class );
            } else if ( nodeName.equals( "flow" ) ) {
                context.convertAnother( segment, Flow.class );
                // Risks
            } else if ( nodeName.equals( "risk" ) ) {
                Risk risk = (Risk) context.convertAnother( segment, Risk.class );
                segment.addRisk( risk );
                // Issues
            } else if ( nodeName.equals( "issue" ) ) {
                context.convertAnother( segment, UserIssue.class );
            } else {
                LOG.warn( "Unknown element " + nodeName );
            }
            reader.moveUp();
        }
        // Remove automatically created default part
        segment.removeNode( defaultPart );
        if ( segment.getPhase() == null ) {
            LOG.warn( "Setting segment's phase to plan default." );
            segment.setPhase( getContext().getPlan().getDefaultPhase( getQueryService() ) );
        }
        Map<String, Object> state = new HashMap<String, Object>();
        state.put( "segment", segment );
        state.put( "idMap", context.get( "idMap" ) );
        state.put( "proxyConnectors", context.get( "proxyConnectors" ) );
        return state;
    }

}
