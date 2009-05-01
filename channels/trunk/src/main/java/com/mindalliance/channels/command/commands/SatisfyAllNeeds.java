package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.Commander;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.CommandUtils;
import com.mindalliance.channels.command.MultiCommand;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.model.Scenario;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Node;
import com.mindalliance.channels.model.Connector;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;

/**
 * Satisfy as many of a part's needs as possible by creating flow from other parts with matching capabilities.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 22, 2009
 * Time: 6:44:30 PM
 */
public class SatisfyAllNeeds extends AbstractCommand {

    public SatisfyAllNeeds() {
    }

    public SatisfyAllNeeds( Part part ) {
        needLockOn( part );
        set( "scenario", part.getScenario().getId() );
        set( "part", part.getId() );
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "satify needs";
    }

    /**
     * {@inheritDoc}
     */
    public boolean canDo( Commander commander ) {
        try {
            Scenario scenario = commander.resolve( Scenario.class, (Long) get( "scenario" ) );
            Part part = (Part) scenario.getNode( commander.resolveId( (Long) get( "part" ) ) );
            if ( part == null ) {
                return false;
            } else {
                List<Flow> unsatisfiedNeeds = commander.getQueryService().findUnsatisfiedNeeds( part );
                return !unsatisfiedNeeds.isEmpty();
            }
        } catch ( CommandException e ) {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    public Change execute( Commander commander ) throws CommandException {
        QueryService queryService = commander.getQueryService();
        try {
            Scenario scenario = commander.resolve( Scenario.class, (Long) get( "scenario" ) );
            Part part = (Part) scenario.getNode( commander.resolveId( (Long) get( "part" ) ) );
            if ( part == null ) throw new NotFoundException();
            List<Flow> unsatisfiedNeeds = queryService.findUnsatisfiedNeeds( part );
            List<Long> addedFlows = new ArrayList<Long>();
            List<Map<String, Object>> removedNeeds = new ArrayList<Map<String, Object>>();
            for ( Flow need : unsatisfiedNeeds ) {
                List<Connector> connectors = queryService.findAllSatificers( need );
                for ( Connector connector : connectors ) {
                    Node source = connector.getScenario() != scenario
                            ? connector
                            : connector.getInnerFlow().getSource();
                    Flow satisfaction = queryService.connect( source, part, need.getName() );
                    satisfaction.initFrom( need );
                    addedFlows.add( satisfaction.getId() );
                }
                if ( !connectors.isEmpty() ) {
                    removedNeeds.add( CommandUtils.getFlowIdentity( need, part ) );
                    need.disconnect();
                }
            }
            set( "addedFlows", addedFlows );
            set( "removedNeeds", removedNeeds );
            return new Change( Change.Type.Recomposed, part.getScenario() );
        } catch ( NotFoundException e ) {
            throw new CommandException( "You need to refresh.", e );
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
    @SuppressWarnings( "unchecked" )
    protected Command doMakeUndoCommand( Commander commander ) throws CommandException {
        MultiCommand multi = new MultiCommand( "satisfy needs" );
        multi.setUndoes( getName() );
        try {
            Scenario scenario = commander.resolve( Scenario.class, (Long) get( "scenario" ) );
            Part part = commander.resolve( Part.class, (Long) get( "part" ) );
            if ( part == null ) throw new NotFoundException();
            // remove added flows
            List<Long> addedFlows = (List<Long>) get( "addedFlows" );
            if ( addedFlows != null ) {
                for ( Long id : addedFlows ) {
                    Flow addedFlow = scenario.findFlow( commander.resolveId( id ) );
                    DisconnectFlow disconnectFlow = new DisconnectFlow( addedFlow );
                    multi.addCommand( disconnectFlow );
                }
            }
            // add removed needs
            List<Map<String, Object>> removedNeeds = (List<Map<String, Object>>) get( "removedNeeds" );
            for ( Map<String, Object> flowIdentity : removedNeeds ) {
                Long removedNeedId = commander.resolveId( (Long) flowIdentity.get( "flow" ) );
                Map<String, Object> state = (Map<String, Object>) flowIdentity.get( "state" );
                Long otherId = (Long) state.get( "other" );
                // other node is a local connector
                assert (otherId == null);
                boolean isOutcome = (Boolean) state.get( "isOutcome" );
                // it's a need
                assert (!isOutcome);
                Command command = new AddNeed();
                command.setArguments( state );
                command.set( "flow", removedNeedId );
                multi.addCommand( command );
            }
            return multi;
        } catch ( NotFoundException e ) {
            throw new CommandException( "You need to refresh.", e );
        }
    }


}
