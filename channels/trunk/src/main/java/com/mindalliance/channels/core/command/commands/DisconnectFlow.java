package com.mindalliance.channels.core.command.commands;

import com.mindalliance.channels.core.command.AbstractCommand;
import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.Command;
import com.mindalliance.channels.core.command.CommandException;
import com.mindalliance.channels.core.command.Commander;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.util.ChannelsUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Disconnect a flow.
 */
public class DisconnectFlow extends AbstractCommand {

    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( DisconnectFlow.class );


    public DisconnectFlow() {
        super( "daemon" );
    }

    public DisconnectFlow( String userName ) {
        super( userName );
    }

    public DisconnectFlow( String userName, Flow flow ) {
        this( userName );
        assert !flow.isNeed() && !flow.isCapability();
        needLocksOn( ChannelsUtils.getLockingSetFor( flow ) );
        set( "segment", flow.getSegment().getId() );
        set( "flow", flow.getId() );
    }

    @Override
    public String getName() {
        return "remove flow";
    }

    @Override
    public Change execute( Commander commander ) throws CommandException {
        try {
            Segment segment = commander.resolve( Segment.class, (Long) get( "segment" ) );
            assert get( "flow" ) != null;
            Flow flow = segment.findFlow( (Long) get( "flow" ) );
            set( "flowState", ChannelsUtils.getFlowConnectionState( flow ) );
            describeTarget( flow );
            Change change = new Change( Change.Type.Removed, flow );
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
    @SuppressWarnings( "unchecked" )
    protected Command makeUndoCommand( Commander commander ) throws CommandException {
        Command connectWithFlow = new ConnectWithFlow( getUserName() );
        connectWithFlow.setArguments( (Map<String, Object>) get( "flowState" ) );
        // The previous id of the flow being re-connected
        connectWithFlow.set( "flow", get( "flow" ) );
        return connectWithFlow;
    }

    @Override
    public String getLabel( Commander commander ) throws CommandException {
        Segment segment = commander.resolve( Segment.class, (Long) get( "segment" ) );
        try {
            Flow flow = segment.findFlow( (Long) get( "flow" ) );
            return flow.isCapability() ? "Remove capability" : flow.isNeed() ? "Remove need" : "Remove flow";
        } catch ( NotFoundException e ) {
            throw new CommandException( "You need to refresh" );
        }
    }
}
