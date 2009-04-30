package com.mindalliance.channels.pages.components.menus;

import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.commands.AddUserIssue;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.list.ListItem;

import java.util.List;
import java.util.ArrayList;

/**
 * Entity panel actions menu.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 24, 2009
 * Time: 12:57:11 PM
 */
public class EntityActionsMenuPanel extends MenuPanel {

    public EntityActionsMenuPanel( String id, IModel<? extends Identifiable> model ) {
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
        return new ArrayList<CommandWrapper>() {
            {
                add( new CommandWrapper( new AddUserIssue( (ModelObject)getModel().getObject() ) ) {
                    public void onExecuted(
                            AjaxRequestTarget target,
                            Change change ) {
                        update( target, change );
                    }
                } );
            }
        };
    }

}
