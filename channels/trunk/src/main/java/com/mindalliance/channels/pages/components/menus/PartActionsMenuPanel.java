package com.mindalliance.channels.pages.components.menus;

import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.command.commands.AddIssue;
import com.mindalliance.channels.command.commands.RemovePart;
import com.mindalliance.channels.command.commands.DuplicatePart;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.list.ListItem;

import java.util.List;
import java.util.ArrayList;

/**
 * Actions menu for a part..
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 11, 2009
 * Time: 1:30:09 PM
 */
public class PartActionsMenuPanel extends MenuPanel {

    public PartActionsMenuPanel( String s, IModel<? extends ModelObject> model ) {
        super( s, model );
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
            private final Scenario scenario = getPart().getScenario();
            {
                add( new CommandWrapper( new AddIssue( getPart() ) ) {
                    public void onExecution( AjaxRequestTarget target, Object result ) {
                        updateWith( target, getPart() );
                    }
                } );
                add( new CommandWrapper( new DuplicatePart( getPart() ) ) {
                    public void onExecution( AjaxRequestTarget target, Object result ) {
                        updateWith( target, scenario );
                    }
                } );
                add( new CommandWrapper( new RemovePart( getPart() ) ) {
                    public void onExecution( AjaxRequestTarget target, Object result ) {
                        updateWith( target, scenario );
                    }
                } );
            }
        };
    }

    private Part getPart() {
        return (Part) getModel().getObject();
    }

}
