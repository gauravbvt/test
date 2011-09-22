/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.core.command.commands;

import com.mindalliance.channels.core.Attachment;
import com.mindalliance.channels.core.command.AbstractCommand;
import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.Command;
import com.mindalliance.channels.core.command.CommandException;
import com.mindalliance.channels.core.command.Commander;
import com.mindalliance.channels.core.dao.PlanDao;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Delay;
import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.Role;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.engine.query.QueryService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Command to add a new part to a plan segment.
 */
public class AddPart extends AbstractCommand {

    public AddPart() {
        this( "daemon" );
    }

    public AddPart( String userName ) {
        super( userName );
    }

    public AddPart( String userName, Segment segment ) {
        this( userName );
        addConflicting( segment );
        set( "segment", segment.getId() );
    }

    public AddPart( String userName, Segment segment, String task ) {
        this( userName, segment );
        set( "task", task );
    }

    @Override
    public String getName() {
        return "add new task";
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public Change execute( Commander commander ) throws CommandException {
        PlanDao planDao = commander.getPlanDao();
        Segment segment = commander.resolve( Segment.class, (Long) get( "segment" ) );
        // Identify any undefined part likely created to be the lone default part.
        Long defaultPartId = (Long) get( "defaultPart" );
        Part defaultPart = null;
        if ( defaultPartId != null ) {
            // A default part was added before removing the one being restored by adding it.
            if ( segment.countParts() == 1 && segment.getDefaultPart().isUndefined() ) {
                defaultPart = segment.getDefaultPart();
            }
        }
        Long priorId = (Long) get( "part" );
        Part part = planDao.createPart( segment, priorId );
        String task = (String) get( "task" );
        if ( task != null ) {
            part.setTask( task );
        }
        set( "part", part.getId() );
        if ( defaultPart != null )
            planDao.removeNode( defaultPart, segment );
        Map<String, Object> partState = (Map<String, Object>) get( "partState" );
        if ( partState != null )
            initFromMap( part, partState, commander.getQueryService() );
        describeTarget( part );
        return new Change( Change.Type.Added, part );
    }

    @Override
    public boolean isUndoable() {
        return true;
    }

    @Override
    protected Command makeUndoCommand( Commander commander ) throws CommandException {
        try {
            Segment segment = commander.resolve( Segment.class, (Long) get( "segment" ) );
            Part part = (Part) segment.getNode( (Long) get( "part" ) );
            if ( part == null )
                throw new NotFoundException();
            return new RemovePart( getUserName(), part );
        } catch ( NotFoundException e ) {
            throw new CommandException( "Can't undo", e );
        }
    }

    @SuppressWarnings( "unchecked" )
    public static void initFromMap( Part part, Map<String, Object> state, QueryService queryService ) {
        part.setDescription( (String) state.get( "description" ) );
        part.setTask( (String) state.get( "task" ) );
        part.setRepeating( (Boolean) state.get( "repeating" ) );
        part.setSelfTerminating( (Boolean) state.get( "selfTerminating" ) );
        part.setTerminatesEventPhase( (Boolean) state.get( "terminatesEventPhase" ) );
        part.setStartsWithSegment( (Boolean) state.get( "startsWithSegment" ) );
        part.setOngoing( (Boolean) state.get( "ongoing" ) );
        part.setRepeatsEvery( (Delay) state.get( "repeatsEvery" ) );
        part.setCompletionTime( (Delay) state.get( "completionTime" ) );
        part.setCategory( (Part.Category) state.get( "category" ) );
        part.setAttachments( new ArrayList<Attachment>( (List<Attachment>) state.get( "attachments" ) ) );
        for ( Map<String, Object> goalMap : (List<Map<String, Object>>) state.get( "goals" ) )
            part.addGoal( queryService.goalFromMap( goalMap ) );
        if ( state.get( "initiatedEvent" ) == null )
            part.setInitiatedEvent( null );
        else
            part.setInitiatedEvent( queryService.findOrCreateType( Event.class,
                                                                   (String) state.get( "initiatedEvent" ) ) );
        if ( state.get( "actor" ) != null )
            part.setActor( queryService.retrieveEntity( Actor.class, state, "actor" ) );
        else
            part.setActor( null );
        if ( state.get( "role" ) != null )
            part.setRole( queryService.retrieveEntity( Role.class, state, "role" ) );
        else
            part.setRole( null );
        if ( state.get( "organization" ) != null )
            part.setOrganization( queryService.retrieveEntity( Organization.class, state, "organization" ) );
        else
            part.setOrganization( null );
        if ( state.get( "jurisdiction" ) != null )
            part.setJurisdiction( queryService.retrieveEntity( Place.class, state, "jurisdiction" ) );
        else
            part.setJurisdiction( null );
        if ( state.get( "location" ) != null )
            part.setLocation( queryService.retrieveEntity( Place.class, state, "location" ) );
        else
            part.setLocation( null );
    }
}
