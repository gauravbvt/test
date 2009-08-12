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
import com.mindalliance.channels.model.Connector;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Node;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Scenario;

import java.util.List;

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
            Part part = (Part) scenario.getNode( (Long) get( "part" ) );
            if ( part == null ) {
                return false;
            } else {
                List<Flow> unsatisfiedNeeds = commander.getQueryService().findUnconnectedNeeds( part );
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
            Part part = (Part) scenario.getNode( (Long) get( "part" ) );
            if ( part == null ) throw new NotFoundException();
            MultiCommand multi = (MultiCommand) get( "subCommands" );
            if ( multi == null ) {
                multi = makeSubCommands( part, queryService );
                set( "subCommands", multi );
            }
            // else command replay
            multi.execute( commander );
            return new Change( Change.Type.Recomposed, part.getScenario() );
        } catch ( NotFoundException e ) {
            throw new CommandException( "You need to refresh.", e );
        }
    }

    private MultiCommand makeSubCommands( Part part, QueryService queryService ) {
        MultiCommand subCommands = new MultiCommand( "satisfy needs - extra" );
        subCommands.setMemorable( false );
        List<Flow> unsatisfiedNeeds = queryService.findUnconnectedNeeds( part );
        for ( Flow need : unsatisfiedNeeds ) {
            List<Connector> connectors = queryService.findAllSatificers( need );
            for ( Connector connector : connectors ) {
                Node source = connector.getScenario() != part.getScenario()
                        ? connector
                        : connector.getInnerFlow().getSource();
                Command connectWithFlow = new ConnectWithFlow( source, part, need.getName() );
                connectWithFlow.set( "attributes", CommandUtils.getFlowAttributes( need ) );
                subCommands.addCommand( connectWithFlow );
            }
        }
        return subCommands;
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
        MultiCommand multi = new MultiCommand( "unsatisfy needs" );
        multi.setUndoes( getName() );
        MultiCommand subCommands = (MultiCommand) get( "subCommands" );
        subCommands.setMemorable( false );
        multi.addCommand( subCommands.getUndoCommand( commander ) );
        return multi;
    }


}
