package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.Node;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.Commander;
import com.mindalliance.channels.model.Scenario;
import com.mindalliance.channels.model.ScenarioObject;
import com.mindalliance.channels.command.CommandException;

/**
 * Command to update a model object contained in a scenario.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 4, 2009
 * Time: 12:41:28 PM
 */
public class UpdateScenarioObject extends UpdateObject {

    public UpdateScenarioObject() {
    }

    public UpdateScenarioObject(
            final Identifiable identifiable,
            final String property,
            final Object value ) {
        this( identifiable, property, value, Action.Set );
    }

    public UpdateScenarioObject(
            final Identifiable identifiable,
            final String property,
            final Object value,
            final Action action ) {
        super( identifiable, property, value, action );
        set( "scenario", ( (ScenarioObject) identifiable ).getScenario().getId() );
        set( "isNode", identifiable instanceof Node );
    }

    protected String getObjectTypeName( String type ) {
        if ( type.contains( "flow" ) )
            return "flow";
        else
            return "part";
    }

    /**
     * {@inheritDoc}
     */
    protected Identifiable getIdentifiable( Commander commander ) throws CommandException {
        try {
            Scenario scenario = commander.resolve( Scenario.class, (Long) get( "scenario" ) );
            boolean isNode = (Boolean) get( "isNode" );
            ScenarioObject scenarioObject;
            if ( isNode ) {
                scenarioObject = scenario.getNode( commander.resolveId( (Long) get( "object" ) ) );
            } else {
                scenarioObject = scenario.findFlow( commander.resolveId( (Long) get( "object" ) ) );
            }
            return scenarioObject;
        } catch ( NotFoundException e ) {
            throw new CommandException( "You need to refresh", e );
        }
    }

    /**
     * {@inheritDoc}
     */
    protected UpdateObject createUndoCommand(
            Identifiable identifiable,
            String property,
            Object value,
            Action action ) {
        return new UpdateScenarioObject( identifiable, property, value, action );
    }


}
