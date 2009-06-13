package com.mindalliance.channels.export.xml;

import com.mindalliance.channels.Channels;
import com.mindalliance.channels.Exporter;
import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Event;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Place;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.model.Risk;
import com.mindalliance.channels.model.Role;
import com.mindalliance.channels.model.Scenario;
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
 * XStream scenario converter.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Dec 16, 2008
 * Time: 1:47:49 PM
 */
public class ScenarioConverter extends AbstractChannelsConverter {

    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( ScenarioConverter.class );

    public ScenarioConverter( Exporter exporter ) {
        super( exporter );
    }

    /**
     * {@inheritDoc}
     */
    public boolean canConvert( Class aClass ) {
        return Scenario.class.isAssignableFrom( aClass );
    }

    /**
     * {@inheritDoc}
     */
    public void marshal( Object object, HierarchicalStreamWriter writer,
                         MarshallingContext context ) {
        Scenario scenario = (Scenario) object;
        Plan plan = Channels.getPlan();
        QueryService queryService = getQueryService();
        boolean exportingInPlan = isExportingPlan( context )  || scenario.isBeingDeleted();
        context.put( "scenario", scenario );
        writer.addAttribute( "plan", plan.getUri() );
        writer.addAttribute( "version", getVersion() );
        writer.addAttribute( "date", new SimpleDateFormat( "yyyy/MM/dd H:mm:ss z" ).format( new Date() ) );
        writer.addAttribute( "id", String.valueOf( scenario.getId() ) );
        writer.addAttribute( "name", scenario.getName() );
        writer.startNode( "description" );
        writer.setValue( scenario.getDescription() );
        writer.endNode();
        exportDetectionWaivers( scenario, writer );
        exportAttachments( scenario, writer );
        if ( !exportingInPlan ) {
            // All entities if not within a plan export
            Iterator<ModelObject> entities = queryService.iterateEntities();
            while ( entities.hasNext() ) {
                ModelObject entity = entities.next();
                writer.startNode( entity.getClass().getSimpleName().toLowerCase() );
                context.convertAnother( entity );
                writer.endNode();
            }
            for ( Event incident : plan.getIncidents() ) {
                writer.startNode( "incident" );
                writer.setValue( incident.getName() );
                writer.endNode();
            }
        }
        // Trigger event
        if ( scenario.getEvent() != null ) {
            writer.startNode( "trigger-event" );
            writer.setValue( scenario.getEvent().getName() );
            writer.endNode();
        }
        // Scenario user issues
        exportUserIssues( scenario, writer, context );
        // Risks in scope
        for ( Risk risk : scenario.getRisks() ) {
            writer.startNode( "risk" );
            context.convertAnother( risk );
            writer.endNode();
        }
        // Parts
        Iterator<Part> parts = scenario.parts();
        while ( parts.hasNext() ) {
            writer.startNode( "part" );
            context.convertAnother( parts.next() );
            writer.endNode();
        }
        // Flows
        Iterator<Flow> flows = scenario.flows();
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
        Scenario scenario = importingPlan
                                ? queryService.createScenario( oldId )
                                : queryService.createScenario();
        Part defaultPart = scenario.getDefaultPart();
        context.put( "scenario", scenario );
        scenario.setName( reader.getAttribute( "name" ) );
        idMap.put( oldId, scenario.getId() );
        while ( reader.hasMoreChildren() ) {
            reader.moveDown();
            String nodeName = reader.getNodeName();
            if ( nodeName.equals( "description" ) ) {
                scenario.setDescription( reader.getValue() );
            } else if ( nodeName.equals( "detection-waivers" ) ) {
                importDetectionWaivers( scenario, reader );
            }  else if ( nodeName.equals( "attachments" ) ) {
                importAttachments( scenario, reader );
            } else if ( nodeName.equals( "event" ) ) {
                context.convertAnother( scenario, Event.class );
            } else if ( nodeName.equals( "actor" ) ) {
                context.convertAnother( scenario, Actor.class );
            } else if ( nodeName.equals( "organization" ) ) {
                context.convertAnother( scenario, Organization.class );
            } else if ( nodeName.equals( "role" ) ) {
                context.convertAnother( scenario, Role.class );
            } else if ( nodeName.equals( "place" ) ) {
                context.convertAnother( scenario, Place.class );
                // Incident
            } else if ( nodeName.equals( "incident" ) ) {
                Event event = getQueryService().findOrCreate( Event.class, reader.getValue() );
                Channels.getPlan().addIncident( event );
                // Event
            } else if ( nodeName.equals( "trigger-event" ) ) {
                Event event = getQueryService().findOrCreate( Event.class, reader.getValue() );
                scenario.setEvent( event );
                // Parts and flows
            } else if ( nodeName.equals( "part" ) ) {
                context.convertAnother( scenario, Part.class );
            } else if ( nodeName.equals( "flow" ) ) {
                context.convertAnother( scenario, Flow.class );
                // Risks
            } else if ( nodeName.equals( "risk" ) ) {
                Risk risk = (Risk) context.convertAnother( scenario, Risk.class );
                scenario.addRisk( risk );
                // Issues
            } else if ( nodeName.equals( "issue" ) ) {
                context.convertAnother( scenario, UserIssue.class );
            } else {
                LOG.warn( "Unknown element " + nodeName );
            }
            reader.moveUp();
        }
        // Remove automatically created default part
        scenario.removeNode( defaultPart );
        Map<String, Object> state = new HashMap<String, Object>();
        state.put( "scenario", scenario );
        state.put( "idMap", context.get( "idMap" ) );
        state.put( "proxyConnectors", context.get( "proxyConnectors" ) );
        return state;
    }

}
