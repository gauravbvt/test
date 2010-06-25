package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.command.Commander;
import com.mindalliance.channels.model.Segment;

/**
 * Command to add a plan segment.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 6, 2009
 * Time: 8:58:12 AM
 */
public class AddSegment extends AbstractCommand {

    public AddSegment() {
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "add new segment";
    }

    /**
     * {@inheritDoc}
     */
    public Change execute( Commander commander ) throws CommandException {
        Long priorId = (Long) get( "segment" );
        Long priorDefaultPartId = (Long) get("defaultPart");
        Segment segment = commander.getQueryService().createSegment(
                priorId,
                priorDefaultPartId);
        commander.getPlan().addSegment( segment );
        set( "segment", segment.getId() );
        set( "defaultPart", segment.getDefaultPart().getId() );
        return new Change( Change.Type.Added, segment );
    }

    /**
     * {@inheritDoc}
     */
    public boolean isUndoable() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    protected Command makeUndoCommand( Commander commander ) throws CommandException {
        return null;
    }
}
