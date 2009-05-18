package com.mindalliance.channels.pages.components.plan.menus;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.commands.AddUserIssue;
import com.mindalliance.channels.command.commands.PasteAttachment;
import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.pages.components.menus.CommandWrapper;
import com.mindalliance.channels.pages.components.menus.MenuPanel;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

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
public class PlanEditActionsMenuPanel extends MenuPanel {

    public PlanEditActionsMenuPanel( String id, IModel<? extends Identifiable> model ) {
        super( id, model, null );
        init();
    }

    private void init() {
        ListView<Component> menuItems = new ListView<Component>(
                "items",
                new PropertyModel<List<Component>>( this, "menuItems" ) ) {
            protected void populateItem( ListItem<Component> item ) {
                item.add( item.getModelObject() );
            }
        };
        add( menuItems );
    }

    /**
     * Get population of menu items.
     *
     * @return a list of menu items
     */
    public List<Component> getMenuItems() {
        List<Component> menuItems = new ArrayList<Component>();
        // Undo and redo
        menuItems.add( this.getUndoMenuItem( "menuItem" ) );
        menuItems.add( this.getRedoMenuItem( "menuItem" ) );
        // Commands
        menuItems.addAll( getCommandMenuItems( "menuItem", getCommandWrappers() ) );
        return menuItems;
    }

    private List<CommandWrapper> getCommandWrappers() {
        List<CommandWrapper> commandWrappers = new ArrayList<CommandWrapper>();
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
        return commandWrappers;
    }

    private Plan getPlan() {
        return (Plan) getModel().getObject();
    }

}
