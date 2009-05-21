package com.mindalliance.channels.pages.components.scenario.menus;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.commands.AddUserIssue;
import com.mindalliance.channels.command.commands.PasteAttachment;
import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.Scenario;
import com.mindalliance.channels.pages.components.menus.ActionMenuPanel;
import com.mindalliance.channels.pages.components.menus.CommandWrapper;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Scenario edit actions menu.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 8, 2009
 * Time: 12:57:58 PM
 */
public class ScenarioActionsMenuPanel extends ActionMenuPanel {

    public ScenarioActionsMenuPanel( String s, IModel<? extends Identifiable> model, Set<Long> expansions ) {
        super( s, model, expansions );
    }

    /**
     * {@inheritDoc}
     */
    protected List<CommandWrapper> getCommandWrappers() {
        List<CommandWrapper> commandWrappers = new ArrayList<CommandWrapper>();
        commandWrappers.add( new CommandWrapper( new PasteAttachment( getScenario() ) ) {
             public void onExecuted( AjaxRequestTarget target, Change change ) {
                 update( target, change );
             }
         } );
        commandWrappers.add( new CommandWrapper( new AddUserIssue( getScenario() ) ) {
            public void onExecuted(
                    AjaxRequestTarget target,
                    Change change ) {
                update( target, change );
            }
        } );
        return commandWrappers;
    }

    private Scenario getScenario() {
        return (Scenario) getModel().getObject();
    }
}
