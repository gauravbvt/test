package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.Service;
import com.mindalliance.channels.NotFoundException;

/**
 * Command to add a new part to a scenario.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 5, 2009
 * Time: 1:06:46 PM
 */
public class AddPart extends AbstractCommand {

    public AddPart( Scenario scenario ) {
        needLockOn( scenario );
        addArgument( "scenario", scenario.getId() );
    }

    /**
      * {@inheritDoc}
      */
    public String getName() {
        return "add part";
    }

    /**
      * {@inheritDoc}
      */
    public Part execute( Service service ) throws CommandException {
        try {
            Scenario scenario = service.find( Scenario.class, (Long)getArgument("scenario") );
            Part part = service.createPart( scenario );
            addArgument( "part", part.getId() );
            return part;
        } catch ( NotFoundException e ) {
            throw new CommandException( "You need to refresh", e);
        }
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
    public Command makeUndoCommand( Service service ) throws CommandException {
        try {
            Scenario scenario = service.find( Scenario.class, (Long)getArgument("scenario") );
            Long partId = (Long)getArgument("part");
            if (partId == null) {
                throw new CommandException( "Can't undo.");
            }
            else {
                Part part = (Part)scenario.getNode( (Long)getArgument("part") );
                return new RemovePart( part );
            }
        } catch ( NotFoundException e ) {
            throw new CommandException( "Can't undo", e);
        }
    }
}
