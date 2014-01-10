package com.mindalliance.channels.pages.components.plan.menus;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.commands.AddUserIssue;
import com.mindalliance.channels.core.command.commands.PasteAttachment;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.pages.components.menus.ActionMenuPanel;
import com.mindalliance.channels.pages.components.menus.CommandWrapper;
import com.mindalliance.channels.pages.components.menus.LinkMenuItem;
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

    @Override
    public String getHelpTopicId() {
        return "actions-plan-details";
    }

 /*   @Override
    public List<LinkMenuItem> getMenuItems() {
        List<LinkMenuItem> menuItems = super.getMenuItems();
        Plan plan = getPlan();
        if ( !isLockedByUser( plan ) && getLockOwner( plan ) != null ) {
            menuItems.add( editedByLinkMenuItem( "menuItem", plan, getLockOwner( plan ) ) );
        }
        return menuItems;
    }
*/
    /**
     * {@inheritDoc}
     */
    @Override
    protected List<CommandWrapper> getCommandWrappers() {
        List<CommandWrapper> commandWrappers = new ArrayList<CommandWrapper>();
        if ( isLockable( ) ) {
            commandWrappers.add( new CommandWrapper( new PasteAttachment( getUser().getUsername(), getPlan() ) ) {
                public void onExecuted( AjaxRequestTarget target, Change change ) {
                    update( target, change );
                }
            } );
            commandWrappers.add( new CommandWrapper( new AddUserIssue( getUser().getUsername(), getPlan() ) ) {
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
