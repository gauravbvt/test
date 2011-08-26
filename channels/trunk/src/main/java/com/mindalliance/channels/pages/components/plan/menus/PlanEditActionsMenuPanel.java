package com.mindalliance.channels.pages.components.plan.menus;

import com.mindalliance.channels.engine.command.Change;
import com.mindalliance.channels.engine.command.commands.AddUserIssue;
import com.mindalliance.channels.engine.command.commands.PasteAttachment;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.pages.components.menus.ActionMenuPanel;
import com.mindalliance.channels.pages.components.menus.CommandWrapper;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Plan edit action menu.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 7, 2009
 * Time: 5:07:23 AM
 */
public class PlanEditActionsMenuPanel extends ActionMenuPanel {

    public PlanEditActionsMenuPanel(
            String id,
            IModel<? extends Identifiable> model ) {
        super( id, model, null );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<CommandWrapper> getCommandWrappers() {
        List<CommandWrapper> commandWrappers = new ArrayList<CommandWrapper>();
        if ( isLockable( ) ) {
            commandWrappers.add( new CommandWrapper( new PasteAttachment( getPlan() ) ) {
                public void onExecuted( AjaxRequestTarget target, Change change ) {
                    update( target, change );
                }
            } );
            commandWrappers.add( new CommandWrapper( new AddUserIssue( getPlan() ) ) {
                public void onExecuted(
                        AjaxRequestTarget target,
                        Change change ) {
                    update( target, change );
                }
            } );
        }
        return commandWrappers;
    }

}
