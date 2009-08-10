package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.Commander;
import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.command.MultiCommand;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Risk;
import com.mindalliance.channels.model.Scenario;

/**
 * Remove a risk from a scenario.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Aug 10, 2009
 * Time: 2:42:34 PM
 */
public class RemoveRisk extends AbstractCommand {

    public RemoveRisk() {
    }

    public RemoveRisk( Scenario scenario, Risk risk ) {
        needLockOn( scenario );
        set( "scenario", scenario.getId() );
        set( "organization", risk.getOrganization().getName() );
        set( "type", risk.getType().name() );
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "delete risk";
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
    public Change execute( Commander commander ) throws CommandException {
        Scenario scenario = commander.resolve( Scenario.class, (Long) get( "scenario" ) );
        Risk.Type type = Risk.Type.valueOf( (String) get( "type" ) );
        Risk risk = scenario.getRisk( type, (String) get( "organization" ) );
        MultiCommand multi = (MultiCommand) get( "subCommands" );
        if ( multi == null ) {
            multi = makeSubCommands( scenario, risk );
            set( "subCommands", multi );
        }
        // else this is a replay
        multi.execute( commander );
        return new Change( Change.Type.Recomposed, scenario );
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( "unchecked" )
    protected Command makeUndoCommand( Commander commander ) throws CommandException {
        MultiCommand multi = new MultiCommand( "undelete risk" );
        multi.setUndoes( getName() );
        MultiCommand subCommands = (MultiCommand) get( "subCommands" );
        subCommands.setMemorable( false );
        multi.addCommand( subCommands.getUndoCommand( commander ) );
        return multi;
    }

    private MultiCommand makeSubCommands( Scenario scenario, Risk risk ) {
        MultiCommand subCommands = new MultiCommand( "delete risk - internal" );
        subCommands.addCommand( new UpdatePlanObject(
                scenario,
                "risks",
                risk,
                UpdateObject.Action.Remove )
        );
        for ( Part part : scenario.getMitigators( risk ) ) {
            subCommands.addCommand( new UpdateScenarioObject(
                    part,
                    "mitigations",
                    risk,
                    UpdateObject.Action.Remove
            ) );
        }
        return subCommands;
    }

}
