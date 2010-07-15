package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.command.Commander;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.NotFoundException;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.util.ChannelsUtils;

/**
 * Copy a flow.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 20, 2009
 * Time: 1:25:19 PM
 */
public class CopyFlow extends AbstractCommand {

    public CopyFlow( Flow flow, Part part ) {
        set( "part", part.getId() );
        set( "flow", flow.getId() );
        set( "segment", flow.getSegment().getId() );
    }

    /**
     * {@inheritDoc}
     */
    public boolean isMemorable() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "copy";
    }

    /**
     * {@inheritDoc}
     */
    public Change execute( Commander commander ) throws CommandException {
        Segment segment = commander.resolve( Segment.class, (Long) get( "segment" ) );
        Flow flow;
        Part part;
        try {
            part = (Part) segment.getNode( (Long) get( "part" ) );
            flow = segment.findFlow( (Long) get( "flow" ) );
        } catch ( NotFoundException e ) {
            throw new CommandException( "You need to refresh", e );
        }
        commander.setCopy( flow.getTarget() == part
                ? ChannelsUtils.getNeedState( flow, part )
                : ChannelsUtils.getCapabilityState( flow, part ) );
        return new Change( Change.Type.None, flow );
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

    /**
     * {@inheritDoc}
     */
    public String getLabel( Commander commander ) throws CommandException {
        Segment segment = commander.resolve( Segment.class, (Long) get( "segment" ) );
        Flow flow;
        try {
            flow = segment.findFlow( (Long) get( "flow" ) );
        } catch ( NotFoundException e ) {
            throw new CommandException( "You need to refresh" );
        }
        if ( flow.isCapability() ) return "Copy capability";
        else if ( flow.isNeed() ) return "Copy need";
        else return "Copy flow";
    }
}
