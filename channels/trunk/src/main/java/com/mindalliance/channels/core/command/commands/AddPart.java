/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.core.command.commands;

import com.mindalliance.channels.core.command.AbstractCommand;
import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.Command;
import com.mindalliance.channels.core.command.CommandException;
import com.mindalliance.channels.core.command.Commander;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.dao.PlanDao;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Segment;

import java.util.Map;

/**
 * Command to add a new part to a segment.
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
        CommunityService communityService = commander.getCommunityService();
        if ( partState != null )
            part.initFromMap( partState, communityService );
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

}
