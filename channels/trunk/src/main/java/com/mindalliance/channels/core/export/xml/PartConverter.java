package com.mindalliance.channels.core.export.xml;

import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.AssignedLocation;
import com.mindalliance.channels.core.model.Delay;
import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Function;
import com.mindalliance.channels.core.model.Goal;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.Role;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.model.UserIssue;
import com.mindalliance.channels.core.model.asset.AssetConnection;
import com.mindalliance.channels.core.model.checklist.Checklist;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public PartConverter( XmlStreamer.Context context ) {
        super( context );
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
        writeTags( writer, part );
        exportDetectionWaivers( part, writer );
        exportAttachments( part, writer );
        writer.startNode( "description" );
        writer.setValue( part.getDescription() );
        writer.endNode();
        if ( part.getTask() != null ) {
            writer.startNode( "task" );
            writer.setValue( part.getTask() );
            writer.endNode();
        }
        if ( part.getFunction() != null ) {
            writer.startNode( "function" );
            writer.addAttribute( "id", Long.toString( part.getFunction().getId() ) );
            writer.addAttribute( "kind", "Type" );
            writer.setValue( part.getFunction().getName() );
            writer.endNode();
        }
        if ( part.getRole() != null ) {
            writer.startNode( "role" );
            writer.addAttribute( "id", Long.toString( part.getRole().getId() ) );
            writer.addAttribute( "kind", "Type" );
            writer.setValue( part.getRole().getName() );
            writer.endNode();
        }
        if ( part.getActor() != null ) {
            writer.startNode( "actor" );
            writer.addAttribute( "id", Long.toString( part.getActor().getId() ) );
            writer.addAttribute( "kind", part.getActor().isType() ? "Type" : "Actual" );
            writer.setValue( part.getActor().getName() );
            writer.endNode();
        }
        if ( part.getOrganization() != null ) {
            writer.startNode( "organization" );
            writer.addAttribute( "id", Long.toString( part.getOrganization().getId() ) );
            writer.addAttribute( "kind", part.getOrganization().isType() ? "Type" : "Actual" );
            writer.setValue( part.getOrganization().getName() );
            writer.endNode();
        }
        if ( part.getLocation() != null ) {
            writer.startNode( "assignedLocation" );
            context.convertAnother( part.getLocation() );
/*
            writer.addAttribute( "id", Long.toString( part.getLocation().getId() ) );
            writer.addAttribute( "kind", part.getLocation().isType() ? "Type" : "Actual" );
            writer.setValue( part.getLocation().getName() );
*/
            writer.endNode();
        }
        if ( part.getJurisdiction() != null ) {
            writer.startNode( "jurisdiction" );
            writer.addAttribute( "id", Long.toString( part.getJurisdiction().getId() ) );
            writer.addAttribute( "kind", part.getJurisdiction().isType() ? "Type" : "Actual" );
            writer.setValue( part.getJurisdiction().getName() );
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
        writer.startNode( "startsWithSegment" );
        writer.setValue( Boolean.toString( part.isStartsWithSegment() ) );
        writer.endNode();
        writer.startNode( "ongoing" );
        writer.setValue( Boolean.toString( part.isOngoing() ) );
        writer.endNode();
        // todo - rename "terminatesEventPhase"
        writer.startNode( "terminatesEvent" );
        writer.setValue( Boolean.toString( part.isTerminatesEventPhase() ) );
        writer.endNode();
        if ( part.getInitiatedEvent() != null ) {
            Event initiatedEvent = part.getInitiatedEvent();
            writer.startNode( "initiatedEvent" );
            writer.addAttribute( "id", Long.toString( initiatedEvent.getId() ) );
            writer.setValue( initiatedEvent.getName() );
            writer.endNode();
        }
        // Part goals --
        for ( Goal goal : part.getGoals() ) {
            writer.startNode( "goal" );
            writer.addAttribute( "intent", goal.isPositive() ? "gain" : "mitigate" );
            writer.addAttribute( "category", goal.getCategory().name() );
            writer.setValue( goal.getOrganization().getName() );
            writer.endNode();
        }
        writer.startNode( "asTeam" );
        writer.setValue( Boolean.toString( part.isAsTeam() ) );
        writer.endNode();
        if ( part.getCategory() != null ) {
            writer.startNode( "category" );
            writer.setValue( part.getCategory().name() );
            writer.endNode();
        }
        // Prohibited
        writer.startNode( "prohibited" );
        writer.setValue( Boolean.toString( part.isProhibited() ) );
        writer.endNode();
        // checklist
        if ( !part.getChecklist().isEmpty() ) {
            writer.startNode( "checklist" );
            context.convertAnother( part.getChecklist() );
            writer.endNode();
        }
        // Asset connections
        for ( AssetConnection assetConnection : part.getAssetConnections().getAll() ) {
            writer.startNode( "assetConnection" );
            context.convertAnother( assetConnection );
            writer.endNode();
        }
        // Part user issues
        exportUserIssues( part, writer, context );
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( "unchecked" )
    public Object unmarshal( HierarchicalStreamReader reader, UnmarshallingContext context ) {
        Segment segment = (Segment) context.get( "segment" );
        Map<Long, Long> idMap = getIdMap( context );
        boolean importingPlan = isImportingPlan( context );
        Long id = Long.parseLong( reader.getAttribute( "id" ) );
        Part part = importingPlan
                ? getModelDao().createPart( segment, id )
                : getModelDao().createPart( segment, null );
        idMap.put( id, part.getId() );
        while ( reader.hasMoreChildren() ) {
            reader.moveDown();
            String nodeName = reader.getNodeName();
            if ( nodeName.equals( "description" ) ) {
                part.setDescription( reader.getValue() );
            } else if ( nodeName.equals( "tags" ) ) {
                part.addTags( reader.getValue() );
            } else if ( nodeName.equals( "detection-waivers" ) ) {
                importDetectionWaivers( part, reader );
            } else if ( nodeName.equals( "attachments" ) ) {
                importAttachments( part, reader );
            } else if ( nodeName.equals( "task" ) ) {
                part.setTask( reader.getValue() );
            } else if ( nodeName.equals( "function" ) ) {
                String idString = reader.getAttribute( "id" );
                part.setFunction( getEntity(
                        Function.class,
                        reader.getValue(),
                        Long.parseLong( idString ),
                        ModelEntity.Kind.Type,
                        context ) );
            } else if ( nodeName.equals( "role" ) ) {
                String idString = reader.getAttribute( "id" );
                part.setRole( getEntity(
                        Role.class,
                        reader.getValue(),
                        Long.parseLong( idString ),
                        ModelEntity.Kind.Type,
                        context ) );
            } else if ( nodeName.equals( "actor" ) ) {
                String idString = reader.getAttribute( "id" );
                ModelEntity.Kind kind = kind( reader.getAttribute( "kind" ) );
                part.setActor( getEntity(
                        Actor.class,
                        reader.getValue(),
                        Long.parseLong( idString ),
                        kind, context ) );
            } else if ( nodeName.equals( "organization" ) ) {
                String idString = reader.getAttribute( "id" );
                ModelEntity.Kind kind = kind( reader.getAttribute( "kind" ) );
                part.setOrganization( getEntity(
                        Organization.class,
                        reader.getValue(),
                        Long.parseLong( idString ),
                        kind, context ) );
            } else if ( nodeName.equals( "location" ) ) {
                // deprecated
                LOG.debug( "The part \"location\" element is deprecated. Use \"assignedLocation\" instead." );
                String idString = reader.getAttribute( "id" );
                ModelEntity.Kind kind = kind( reader.getAttribute( "kind" ) );
                AssignedLocation assignedLocation = new AssignedLocation();
                assignedLocation.setKind( AssignedLocation.Kind.NamedPlace );
                assignedLocation.setNamedPlace( getEntity(
                        Place.class,
                        reader.getValue(),
                        Long.parseLong( idString ),
                        kind, context ) );
                part.setLocation( assignedLocation );
            } else if ( nodeName.equals( "assignedLocation" ) ) {
                AssignedLocation assignedLocation = (AssignedLocation) context.convertAnother( part, AssignedLocation.class );
                part.setLocation( assignedLocation );
            } else if ( nodeName.equals( "jurisdiction" ) ) {
                String idString = reader.getAttribute( "id" );
                ModelEntity.Kind kind = kind( reader.getAttribute( "kind" ) );
                part.setJurisdiction( getEntity(
                        Place.class,
                        reader.getValue(),
                        Long.parseLong( idString ),
                        kind, context ) );
            } else if ( nodeName.equals( "completionTime" ) ) {
                part.setCompletionTime( Delay.parse( reader.getValue() ) );
            } else if ( nodeName.equals( "repeatsEvery" ) ) {
                part.setRepeatsEvery( Delay.parse( reader.getValue() ) );
            } else if ( nodeName.equals( "startsWithSegment" ) ) {
                part.setStartsWithSegment( reader.getValue().equals( "true" ) );
            } else if ( nodeName.equals( "ongoing" ) ) {
                part.setOngoing( reader.getValue().equals( "true" ) );
            } else if ( nodeName.equals( "terminatesEvent" ) ) {
                part.setTerminatesEventPhase( reader.getValue().equals( "true" ) );
            } else if ( nodeName.equals( "initiatedEvent" ) ) {
                String eventId = reader.getAttribute( "id" );
                String eventName = reader.getValue();
                Event event = findOrCreateType( Event.class, eventName, eventId );
                if ( event == null ) LOG.warn( "Model has no event named " + eventName );
                part.setInitiatedEvent( event );
            } else if ( nodeName.equals( "flow" ) ) {
                context.convertAnother( segment, Flow.class );
            } else if ( nodeName.equals( "goal" ) ) {
                boolean positive = reader.getAttribute( "intent" ).equals( "gain" );
                Goal.Category category = Goal.Category.valueOf( reader.getAttribute( "category" ) );
                String orgName = reader.getValue();
                Goal goal = segment.getGoal( category, positive, orgName );
                if ( goal == null ) {
                    LOG.error( "Goal not found: " + category + " " + positive + " " + orgName );
                } else {
                    part.getGoals().add( goal );
                }
            } else if ( nodeName.equals( "asTeam" ) ) {
                part.setAsTeam( reader.getValue().equals( "true" ) );
            } else if ( nodeName.equals( "issue" ) ) {
                context.convertAnother( segment, UserIssue.class );
            } else if ( nodeName.equals( "category" ) ) {
                part.setCategory( Part.Category.valueOf( reader.getValue() ) );
            } else if ( nodeName.equals( "prohibited" ) ) {
                part.setProhibited( reader.getValue().equals( "true" ) );
            } else if ( nodeName.equals( "checklist" ) ) {
                Checklist checklist = (Checklist) context.convertAnother( part, Checklist.class );
                checklist.setPart( part );
                part.setChecklist( checklist );
            } else if ( nodeName.equals( "assetConnection" ) ) {
                AssetConnection assetConnection = (AssetConnection) context.convertAnother( part, AssetConnection.class );
                part.addAssetConnection( assetConnection );
            } else {
                LOG.debug( "Unknown element " + nodeName );
            }
            reader.moveUp();
        }
        return part;
    }

}
