package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.Commander;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.command.CommandUtils;
import com.mindalliance.channels.command.MultiCommand;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Scenario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Paste copied part.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 20, 2009
 * Time: 3:14:01 PM
 */
public class PastePart extends AbstractCommand {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( PastePart.class );

    public PastePart() {
    }

    public PastePart( Scenario scenario ) {
        needLockOn( scenario );
        set( "scenario", scenario.getId() );
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "paste part";
    }

    /**
     * {@inheritDoc}
     */
    public boolean canDo( Commander commander ) {
        return super.canDo( commander ) && commander.isPartCopied();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( "unchecked" )
    public Change execute( Commander commander ) throws CommandException {
        QueryService queryService = commander.getQueryService();
        Map<String, Object> copy;
        if ( commander.isReplaying() ) {
            copy = (Map<String, Object>) get( "copy" );
        } else {
            copy = commander.getCopy();
            set( "copy", copy );
        }
        Map<String, Object> partState = (Map<String, Object>) copy.get( "partState" );
        Scenario scenario = commander.resolve( Scenario.class, (Long) get( "scenario" ) );
        Long priorId = (Long) get( "part" );
        Part part = queryService.createPart( scenario, priorId );
        set( "part", part.getId() );
        CommandUtils.initPartFrom( part, partState, commander );
        commander.getAttachmentManager().reattachAll( part.getAttachmentTickets() );
        List<Map<String, Object>> needStates = (List<Map<String, Object>>) copy.get( "needs" );
        List<Long> addedNeeds = new ArrayList<Long>();
        for ( Map<String, Object> needState : needStates ) {
            Flow need = queryService.connect(
                    queryService.createConnector( scenario ),
                    part,
                    (String) needState.get( "name" ) );
            CommandUtils.initialize( need, (Map<String, Object>) needState.get( "attributes" ) );
            commander.getAttachmentManager().reattachAll( need.getAttachmentTickets() );
            addedNeeds.add( need.getId() );
        }
        set( "addedNeeds", addedNeeds );
        List<Map<String, Object>> capabilityStates =
                (List<Map<String, Object>>) copy.get( "capabilities" );
        List<Long> addedCapabilities = new ArrayList<Long>();
        for ( Map<String, Object> capabilityState : capabilityStates ) {
            Flow capability = queryService.connect(
                    part,
                    queryService.createConnector( scenario ),
                    (String) capabilityState.get( "name" ) );
            CommandUtils.initialize(
                    capability,
                    (Map<String, Object>) capabilityState.get( "attributes" ) );
            commander.getAttachmentManager().reattachAll( capability.getAttachmentTickets() );
            addedCapabilities.add( capability.getId() );
        }
        set( "addedCapabilities", addedCapabilities );
        return new Change( Change.Type.Recomposed, scenario );
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
    @SuppressWarnings( "unchecked" )
    protected Command doMakeUndoCommand( Commander commander ) throws CommandException {
        MultiCommand multi = new MultiCommand( "paste part" );
        multi.setUndoes( getName() );
        Part part;
        try {
            Scenario scenario = commander.resolve( Scenario.class, (Long) get( "scenario" ) );
            Long partId = (Long) get( "part" );
            if ( partId == null ) {
                throw new CommandException( "Can't undo." );
            } else {
                part = (Part) scenario.getNode( partId );
                if ( part == null ) throw new NotFoundException();
            }
            // Disconnect any added needs and capabilities - don't fail if not found
            List<Long> addedNeeds = (List<Long>) get( "addedNeeds" );
            if ( addedNeeds != null ) {
                for ( Long flowId : addedNeeds ) {
                    if ( flowId != null ) {
                        Flow flow = scenario.findFlow( flowId );
                        multi.addCommand( new RemoveNeed( flow ) );
                    } else {
                        LOG.info( "Info need not found at " + flowId );
                    }
                }
            }
            List<Long> addedCapabilities = (List<Long>) get( "addedCapabilities" );
            if ( addedCapabilities != null ) {
                for ( Long flowId : addedCapabilities ) {
                    if ( flowId != null ) {
                        Flow flow = scenario.findFlow( flowId );
                        multi.addCommand( new RemoveCapability( flow ) );
                    } else {
                        LOG.info( "Info capability not found at " + flowId );
                    }
                }
            }
            // Remove part
            multi.addCommand( new RemovePart( part ) );
            return multi;
        } catch ( NotFoundException e ) {
            throw new CommandException( "You need to refresh.", e );
        }
    }

}
