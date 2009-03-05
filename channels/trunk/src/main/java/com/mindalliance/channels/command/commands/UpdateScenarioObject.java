package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.ScenarioObject;
import com.mindalliance.channels.Node;
import com.mindalliance.channels.pages.Project;

import java.util.HashMap;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 4, 2009
 * Time: 12:41:28 PM
 */
public class UpdateScenarioObject extends AbstractCommand {

    public UpdateScenarioObject( final ScenarioObject scenarioObject, final String property, final Object value ) {
        this.addConflicting( scenarioObject );
        this.needLockOn( scenarioObject );
        this.setArguments( new HashMap<String, Object>() {
            {
                put( "scenario", scenarioObject.getScenario().getId() );
                put( "object", scenarioObject.getId() );
                put( "isNode", scenarioObject instanceof Node );
                put( "property", property );
                put( "value", value );
                put( "old", getProperty( scenarioObject, property ) );
            }
        } );

    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "update " + ((Boolean)getArgument("isNode") ? "part" : "flow");
    }

    /**
      * {@inheritDoc}
      */
    public Object execute() throws CommandException {
        ScenarioObject scenarioObject = getScenarioObject();
        setProperty(
                scenarioObject,
                (String) getArgument( "property" ),
                getArgument( "value" )
        );
        return getArgument( "value" );
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
    public Command makeUndoCommand() throws CommandException {
            ScenarioObject scenarioObject = getScenarioObject();
            String property = (String) getArgument( "property" );
            Object oldValue = getArgument( "old" );
            return new UpdateScenarioObject( scenarioObject, property, oldValue );
    }

    private ScenarioObject getScenarioObject() throws CommandException {
        try {
            Scenario scenario = Project.service().find( Scenario.class, (Long) getArgument( "scenario" ) );
            boolean isNode = (Boolean) getArgument( "isNode" );
            ScenarioObject scenarioObject;
            if ( isNode ) {
                scenarioObject = scenario.getNode( (Long) getArgument( "object" ) );
            } else {
                scenarioObject = scenario.findFlow( (Long) getArgument( "object" ) );
            }
            return scenarioObject;
        } catch ( NotFoundException e ) {
            throw new CommandException( "Can't undo" );
        }
    }
}
