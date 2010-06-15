package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.command.Commander;
import com.mindalliance.channels.dao.NotFoundException;
import com.mindalliance.channels.dao.PlanDao;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.util.ChannelsUtils;

import java.util.Map;

/**
 * Command to add a new part to a plan segment.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 5, 2009
 * Time: 1:06:46 PM
 */
public class AddPart extends AbstractCommand {

    public AddPart() {
    }

    public AddPart( Segment segment ) {
        super();
        set( "segment", segment.getId() );
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "add new task";
    }

    /**
     * {@inheritDoc}
     */
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
        set( "part", part.getId() );
        if ( defaultPart != null ) segment.removeNode( defaultPart, planDao );
        Map<String, Object> partState = (Map<String, Object>) get( "partState" );
        if ( partState != null ) {
            ChannelsUtils.initPartFrom( part, partState, commander );
        }
        return new Change( Change.Type.Added, part );
    }

    /**
     * {@inheritDoc}
     */
    public boolean isUndoable() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    protected Command makeUndoCommand( Commander commander ) throws CommandException {
        try {
            Segment segment = commander.resolve( Segment.class, (Long) get( "segment" ) );
            Part part = (Part) segment.getNode( (Long) get( "part" ) );
            if ( part == null ) throw new NotFoundException();
            return new RemovePart( part );
        } catch ( NotFoundException e ) {
            throw new CommandException( "Can't undo", e );
        }
    }
}
