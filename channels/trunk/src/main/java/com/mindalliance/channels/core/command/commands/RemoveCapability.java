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
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.util.ChannelsUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemoveCapability extends AbstractCommand {

    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( RemoveCapability.class );


    public RemoveCapability() {
        super( "daemon" );
    }

    public RemoveCapability( String userName, Flow flow ) {
        super( userName );
        needLocksOn( ChannelsUtils.getLockingSetFor( flow ) );
        setArguments( ChannelsUtils.getFlowConnectionState( flow ) );
        set( "flow", flow.getId() );
    }

    @Override
    public String getName() {
        return "remove sharing capability";
    }

    @Override
    public Change execute( Commander commander ) throws CommandException {
        try {
            Segment segment = commander.resolve( Segment.class, (Long) get( "segment" ) );
            Flow flow = segment.findFlow( (Long) get( "flow" ) );
            describeTarget( flow );
            Change change = new Change( Type.Removed, flow );
            commander.getPlanDao().disconnect( flow );
            releaseAnyLockOn( commander, flow );
            return change;
        } catch ( NotFoundException e ) {
            throw new CommandException( "You need to refresh.", e );
        }
    }

    @Override
    public boolean canDo( Commander commander ) {
        Segment segment = null;
        try {
            segment = commander.resolve( Segment.class, (Long) get( "segment" ) );
        } catch( CommandException e ) {
            LOG.warn( "Segment not found", e );
            return false;
        }
        return super.canDo( commander )
                && segment.isModifiabledBy( getUserName(), commander.getCommunityService() );
    }


    @Override
    public boolean isUndoable() {
        return true;
    }

    @Override
    protected Command makeUndoCommand( Commander commander ) throws CommandException {
        AddCapability command = new AddCapability( getUserName() );
        command.setArguments( getArguments() );
        return command;
    }
}
