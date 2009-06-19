package com.mindalliance.channels.export.xml;

import com.mindalliance.channels.Channels;
import com.mindalliance.channels.Exporter;
import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Event;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Place;
import com.mindalliance.channels.model.Plan;
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

    public PlanConverter( Exporter exporter ) {
        super( exporter );
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
        writer.addAttribute( "id", "" + plan.getId() );
        writer.addAttribute( "uri", plan.getUri() );
        writer.addAttribute( "version", getVersion() );
        writer.addAttribute( "date", new SimpleDateFormat( "yyyy/MM/dd H:mm:ss z" ).format( new Date() ) );
        writer.startNode( "lastId" );
        writer.setValue( "" + getQueryService().getLastAssignedId() );
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
        exportDetectionWaivers( plan, writer );
        exportAttachments( plan, writer );
        context.put( "exporting-plan", "true" );
        // All entities
        Iterator<ModelObject> entities = queryService.iterateEntities();
        while ( entities.hasNext() ) {
            ModelObject entity = entities.next();
            writer.startNode( entity.getClass().getSimpleName().toLowerCase() );
            context.convertAnother( entity );
            writer.endNode();
        }
        // All incidents
        for ( Event event : plan.getIncidents() ) {
            writer.startNode( "incident" );
            writer.addAttribute( "id", Long.toString( event.getId() ) );
            writer.setValue( event.getName() );
            writer.endNode();
        }
        // All scenarios
        for ( Scenario scenario : plan.getScenarios() ) {
            writer.startNode( "scenario" );
            context.convertAnother( scenario, new ScenarioConverter( getExporter() ) );
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
        Plan plan = Channels.getPlan();
        QueryService queryService = getQueryService();
        String uri = reader.getAttribute( "uri" );
        plan.setUri( uri );
        Long id = Long.parseLong( reader.getAttribute( "id" ) );
        plan.setId( id );
        while ( reader.hasMoreChildren() ) {
            reader.moveDown();
            String nodeName = reader.getNodeName();
            if ( nodeName.equals( "lastId" ) ) {
                Long lastId = Long.parseLong( reader.getValue() );
                queryService.setLastAssignedId( lastId );
            } else if ( nodeName.equals( "name" ) ) {
                plan.setName( reader.getValue() );
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
            } else if ( nodeName.equals( "incident" ) ) {
                String eventId = reader.getAttribute( "id");
                Event event = findOrCreate( Event.class, reader.getValue(), eventId );
                plan.addIncident( event );
                // Scenarios
            } else if ( nodeName.equals( "scenario" ) ) {
                context.convertAnother( plan, Scenario.class );
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
