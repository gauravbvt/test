/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.core.command.commands;

import com.mindalliance.channels.core.command.AbstractCommand;
import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.Change.Type;
import com.mindalliance.channels.core.command.Command;
import com.mindalliance.channels.core.command.CommandException;
import com.mindalliance.channels.core.command.Commander;
import com.mindalliance.channels.core.model.CollaborationModel;

public class RemoveProducer extends AbstractCommand {

    public RemoveProducer() {
        super( "daemon" );
    }

    public RemoveProducer( String userName, CollaborationModel collaborationModel, String producer ) {
        super( userName );
        needLockOn( collaborationModel );
        set( "producer", producer );
    }

    @Override
    public String getName() {
        return "vote not to put in production";
    }

    @Override
    public Change execute( Commander commander ) throws CommandException {
        String producer = (String) get( "producer" );
        CollaborationModel collaborationModel = commander.getPlan();
        collaborationModel.removeProducer( producer );
        setTargetDescription( producer );
        return new Change( Type.Updated, collaborationModel, "producers" );
    }

    @Override
    public boolean isUndoable() {
        return false;
    }

    @Override
    protected Command makeUndoCommand( Commander commander ) throws CommandException {
        return null;
    }

    @Override
    public boolean forcesSnapshot() {
        return true;
    }

}
