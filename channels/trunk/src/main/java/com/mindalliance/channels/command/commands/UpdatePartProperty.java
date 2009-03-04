package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.pages.Project;

import java.util.HashMap;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 4, 2009
 * Time: 12:41:28 PM
 */
public class UpdatePartProperty extends AbstractCommand {

    public UpdatePartProperty( final Part part, final String property, final Object value ) {
        this.addConflicting( part );
        this.needLockOn( part );
        this.setArguments( new HashMap<String, Object>() {
            {
                put( "scenarioId", part.getScenario().getId() );
                put( "partId", part.getId() );
                put( "property", property );
                put( "value", value );
                put( "old", getProperty( part, property ) );
            }
        } );

    }

    public String getName() {
        return "update part";
    }

    public Object execute() throws NotFoundException {
        Scenario scenario = Project.service().find( Scenario.class, (Long) getArgument( "scenarioId" ) );
        Part part = (Part) scenario.getNode( (Long) getArgument( "partId" ) );
        setProperty(
                part,
                (String) getArgument( "property" ),
                getArgument( "value" )
        );
        return null;
    }

    public boolean isUndoable() {
        return true;
    }

    public Command makeUndoCommand() throws CommandException {
        try {
            Scenario scenario = Project.service().find( Scenario.class, (Long) getArgument( "scenarioId" ) );
            Part part = (Part) scenario.getNode( (Long) getArgument( "partId" ) );
            String property = (String)getArgument("property");
            Object oldValue = getArgument("old");
            return new UpdatePartProperty( part, property, oldValue );
        } catch ( NotFoundException e ) {
            throw new CommandException("Can't undo");
        }
    }
}
