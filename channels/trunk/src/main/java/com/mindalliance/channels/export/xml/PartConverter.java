package com.mindalliance.channels.export.xml;

import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Delay;
import com.mindalliance.channels.model.Event;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.ModelEntity;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Place;
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
        exportDetectionWaivers( part, writer );
        exportAttachments( part, writer );
        if ( part.getTask() != null ) {
            writer.startNode( "task" );
            writer.setValue( part.getTask() );
            writer.endNode();
        }
        if ( part.getRole() != null ) {
            writer.startNode( "role" );
            writer.addAttribute( "id", Long.toString( part.getRole().getId() ) );
            writer.addAttribute( "kind",  "Type" );
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
            writer.startNode( "location" );
            writer.addAttribute( "id", Long.toString( part.getLocation().getId() ) );
            writer.addAttribute( "kind", part.getLocation().isType() ? "Type" : "Actual" );
            writer.setValue( part.getLocation().getName() );
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
        writer.setValue( "" + part.setIsStartsWithSegment() );
        writer.endNode();
        // todo - rename "terminatesEventPhase"
        writer.startNode( "terminatesEvent" );
        writer.setValue( "" + part.isTerminatesEventPhase() );
        writer.endNode();
        if ( part.getInitiatedEvent() != null ) {
            Event initiatedEvent = part.getInitiatedEvent();
            writer.startNode( "initiatedEvent" );
            writer.addAttribute( "id", Long.toString( initiatedEvent.getId() ) );
            writer.setValue( initiatedEvent.getName() );
            writer.endNode();
        }
        // Part mitigations
        for ( Risk risk : part.getMitigations() ) {
            writer.startNode( "mitigation" );
            writer.addAttribute( "type", risk.getType().name() );
            writer.setValue( risk.getOrganization().getName() );
            writer.endNode();
        }
        writer.startNode( "asTeam" );
        writer.setValue( "" + part.isAsTeam() );
        writer.endNode();
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
                ? getQueryService().createPart( segment, id )
                : getQueryService().createPart( segment );
        idMap.put( id, part.getId() );
        while ( reader.hasMoreChildren() ) {
            reader.moveDown();
            String nodeName = reader.getNodeName();
            if ( nodeName.equals( "description" ) ) {
                part.setDescription( reader.getValue() );
            } else if ( nodeName.equals( "detection-waivers" ) ) {
                importDetectionWaivers( part, reader );
            } else if ( nodeName.equals( "attachments" ) ) {
                importAttachments( part, reader );
            } else if ( nodeName.equals( "task" ) ) {
                part.setTask( reader.getValue() );
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
                String idString = reader.getAttribute( "id" );
                ModelEntity.Kind kind = kind( reader.getAttribute( "kind" ) );
                part.setLocation( getEntity(
                        Place.class,
                        reader.getValue(),
                        Long.parseLong( idString ),
                        kind, context ) );
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
                part.setIsStartsWithSegment( reader.getValue().equals( "true" ) );
            } else if ( nodeName.equals( "terminatesEvent" ) ) {
                part.setTerminatesEventPhase( reader.getValue().equals( "true" ) );
            } else if ( nodeName.equals( "initiatedEvent" ) ) {
                String eventId = reader.getAttribute( "id" );
                String eventName = reader.getValue();
                Event event = findOrCreateType( Event.class, eventName, eventId );
                if ( event == null ) LOG.warn( "Plan has no event named " + eventName );
                part.setInitiatedEvent( event );
            } else if ( nodeName.equals( "flow" ) ) {
                context.convertAnother( segment, Flow.class );
            } else if ( nodeName.equals( "mitigation" ) ) {
                Risk.Type type = Risk.Type.valueOf( reader.getAttribute( "type" ) );
                String orgName = reader.getValue();
                Risk risk = segment.getRisk( type, orgName );
                part.getMitigations().add( risk );
            }  else if ( nodeName.equals( "asTeam" ) ) {
                part.setAsTeam( reader.getValue().equals( "true" ) );
            } else if ( nodeName.equals( "issue" ) ) {
                context.convertAnother( segment, UserIssue.class );
            } else {
                LOG.warn( "Unknown element " + nodeName );
            }
            reader.moveUp();
        }
        return part;
    }

    private ModelEntity.Kind kind( String attribute ) {
        if ( attribute == null ) return ModelEntity.Kind.Actual;
        if ( attribute.equals( "Type" ) ) return ModelEntity.Kind.Type;
        return ModelEntity.Kind.Actual;
    }

}
