package com.mindalliance.channels.export.xml;

import com.mindalliance.channels.Exporter;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Delay;
import com.mindalliance.channels.model.Event;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Place;
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

import java.util.List;
import java.util.Map;

/**
 * An XStream Part converter.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Dec 16, 2008
 * Time: 3:12:16 PM
 */
public class PartConverter extends AbstractChannelsConverter {

    /**
     * Class logger.
     */
    public static final Logger LOG = LoggerFactory.getLogger( PartConverter.class );

    public PartConverter( Exporter exporter ) {
        super( exporter );
    }

    /**
     * {@inheritDoc}
     */
    public boolean canConvert( Class aClass ) {
        return Part.class.isAssignableFrom( aClass );
    }

    /**
     * {@inheritDoc}
     */
    public void marshal( Object object, HierarchicalStreamWriter writer,
                         MarshallingContext context ) {
        Part part = (Part) object;
        writer.addAttribute( "id", String.valueOf( part.getId() ) );
//        writer.addAttribute( "scenario", String.valueOf( part.getScenario().getId() ) );
        exportDetectionWaivers( part, writer );
        if ( part.getTask() != null ) {
            writer.startNode( "task" );
            writer.setValue( part.getTask() );
            writer.endNode();
        }
        if ( part.getRole() != null ) {
            writer.startNode( "role" );
            context.convertAnother( part.getRole() );
            writer.endNode();
        }
        if ( part.getActor() != null ) {
            writer.startNode( "actor" );
            context.convertAnother( part.getActor() );
            writer.endNode();
        }
        if ( part.getOrganization() != null ) {
            writer.startNode( "organization" );
            context.convertAnother( part.getOrganization() );
            writer.endNode();
        }
        if ( part.getLocation() != null ) {
            writer.startNode( "location" );
            context.convertAnother( part.getLocation() );
            writer.endNode();
        }
        if ( part.getJurisdiction() != null ) {
            writer.startNode( "jurisdiction" );
            context.convertAnother( part.getJurisdiction() );
            writer.endNode();
        }
        if ( part.isSelfTerminating() ) {
            writer.startNode( "completionTime" );
            writer.setValue( part.getCompletionTime().toString() );
            writer.endNode();
        }
        if ( part.isRepeating() ) {
            writer.startNode( "repeatsEvery" );
            writer.setValue( part.getRepeatsEvery().toString() );
            writer.endNode();
        }
        writer.startNode( "startsWithScenario" );
        writer.setValue( "" + part.isStartsWithScenario() );
        writer.endNode();
        writer.startNode( "terminatesEvent" );
        writer.setValue( "" + part.isTerminatesEvent() );
        writer.endNode();
        if ( part.getInitiatedEvent() != null ) {
            writer.startNode( "initiatedEvent" );
            writer.setValue( part.getInitiatedEvent().getName() );
            writer.endNode();
        }
        // Part mitigations
        for ( Risk risk : part.getMitigations() ) {
            writer.startNode( "mitigation" );
            writer.addAttribute( "type", risk.getType().name() );
            writer.setValue( risk.getOrganization().getName() );
            writer.endNode();
        }
        // Part user issues
        List<Issue> issues = getQueryService().findAllUserIssues( part );
        for ( Issue issue : issues ) {
            writer.startNode( "issue" );
            context.convertAnother( issue );
            writer.endNode();
        }
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( "unchecked" )
    public Object unmarshal( HierarchicalStreamReader reader, UnmarshallingContext context ) {
        Scenario scenario = (Scenario) context.get( "scenario" );
        Part part = getQueryService().createPart( scenario );
        Map<String, Long> idMap = (Map<String, Long>) context.get( "idMap" );
        String id = reader.getAttribute( "id" );
        idMap.put( id, part.getId() );
        while ( reader.hasMoreChildren() ) {
            reader.moveDown();
            String nodeName = reader.getNodeName();
            if ( nodeName.equals( "description" ) ) {
                part.setDescription( reader.getValue() );
            } else if ( nodeName.equals( "detection-waivers" ) ) {
                importDetectionWaivers( part, reader );
            } else if ( nodeName.equals( "task" ) ) {
                part.setTask( reader.getValue() );
            } else if ( nodeName.equals( "role" ) ) {
                Role role = (Role) context.convertAnother( scenario, Role.class );
                part.setRole( role );
            } else if ( nodeName.equals( "actor" ) ) {
                Actor actor = (Actor) context.convertAnother( scenario, Actor.class );
                part.setActor( actor );
            } else if ( nodeName.equals( "organization" ) ) {
                Organization organization = (Organization) context.convertAnother( scenario,
                        Organization.class );
                part.setOrganization( organization );
            } else if ( nodeName.equals( "location" ) ) {
                Place location = (Place) context.convertAnother( scenario, Place.class );
                part.setLocation( location );
            } else if ( nodeName.equals( "jurisdiction" ) ) {
                Place jurisdiction =
                        (Place) context.convertAnother( scenario, Place.class );
                part.setJurisdiction( jurisdiction );
            } else if ( nodeName.equals( "completionTime" ) ) {
                part.setCompletionTime( Delay.parse( reader.getValue() ) );
            } else if ( nodeName.equals( "repeatsEvery" ) ) {
                part.setRepeatsEvery( Delay.parse( reader.getValue() ) );
            } else if ( nodeName.equals( "startsWithScenario" ) ) {
                part.setStartsWithScenario( reader.getValue().equals( "true" ) );
            } else if ( nodeName.equals( "terminatesEvent" ) ) {
                part.setTerminatesEvent( reader.getValue().equals( "true" ) );
            } else if ( nodeName.equals( "initiatedEvent" ) ) {
                String eventName = reader.getValue();
                Event event = getQueryService().findOrCreate( Event.class, eventName );
                if ( event == null ) LOG.warn( "Plan has no event named " + eventName );
                part.setInitiatedEvent( event );
            } else if ( nodeName.equals( "flow" ) ) {
                context.convertAnother( scenario, Flow.class );
            } else if ( nodeName.equals( "mitigation" ) ) {
                Risk.Type type = Risk.Type.valueOf( reader.getAttribute( "type" ) );
                String orgName = reader.getValue();
                Risk risk = scenario.getRisk( type, orgName );
                part.getMitigations().add( risk );
            } else if ( nodeName.equals( "issue" ) ) {
                context.convertAnother( scenario, UserIssue.class );
            } else {
                LOG.warn( "Unknown element " + nodeName );
            }
            reader.moveUp();
        }
        return part;
    }

}
