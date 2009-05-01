package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.Commander;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.CommandUtils;
import com.mindalliance.channels.command.MultiCommand;
import com.mindalliance.channels.model.Scenario;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.NotFoundException;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        super();
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
        return commander.isPartCopied();
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
        Map<String, Object> partState = (Map<String, Object>)copy.get( "partState" );
        Scenario scenario = commander.resolve( Scenario.class, (Long) get( "scenario" ) );
        Part part = queryService.createPart( scenario );
        if ( get( "part" ) != null )
            commander.mapId( (Long) get( "part" ), part.getId() );
        set( "part", part.getId() );
        CommandUtils.initialize( part, partState );
        List<Map<String, Object>> needStates = (List<Map<String, Object>>) copy.get( "needs" );
        List<Long> addedNeeds = new ArrayList<Long>();
        for ( Map<String, Object> needState : needStates ) {
            Flow need = queryService.connect(
                    queryService.createConnector( scenario ),
                    part,
                    (String) needState.get( "name" ) );
            CommandUtils.initialize( need, (Map<String, Object>) needState.get( "attributes" ) );
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
                part = (Part) scenario.getNode( commander.resolveId( partId ) );
                if ( part == null ) throw new NotFoundException();
            }
            // Disconnect any added needs and capabilities - don't fail if not found
            List<Long> addedNeeds = (List<Long>) get( "addedNeeds" );
            if ( addedNeeds != null ) {
                for ( long id : addedNeeds ) {
                    // It may not have been put in snapshot yet.
                    Long flowId = commander.resolveId( id );
                    if ( flowId != null ) {
                        Flow flow = scenario.findFlow( flowId );
                        multi.addCommand( new RemoveNeed( flow ) );
                    } else {
                        LOG.info( "Info need not found at " + id );
                    }
                }
            }
            List<Long> addedCapabilities = (List<Long>) get( "addedCapabilities" );
            if ( addedCapabilities != null ) {
                for ( long id : addedCapabilities ) {
                    // It may not have been put in snapshot yet.
                    Long flowId = commander.resolveId( id );
                    if ( flowId != null ) {
                        Flow flow = scenario.findFlow( flowId );
                        multi.addCommand( new RemoveCapability( flow ) );
                    } else {
                        LOG.info( "Info capability not found at " + id );
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
