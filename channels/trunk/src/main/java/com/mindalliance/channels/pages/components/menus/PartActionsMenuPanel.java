package com.mindalliance.channels.pages.components.menus;

import com.mindalliance.channels.Part;
import com.mindalliance.channels.Actor;
import com.mindalliance.channels.Role;
import com.mindalliance.channels.Place;
import com.mindalliance.channels.Organization;
import com.mindalliance.channels.command.commands.AddUserIssue;
import com.mindalliance.channels.command.commands.RemovePart;
import com.mindalliance.channels.command.commands.DuplicatePart;
import com.mindalliance.channels.command.commands.CopyPart;
import com.mindalliance.channels.command.commands.PasteFlow;
import com.mindalliance.channels.command.Change;
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

    public PartActionsMenuPanel( String s, IModel<? extends Part> model ) {
        super( s, model, null );
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
        commandWrappers.add( new CommandWrapper( new CopyPart( getPart() ) ) {
            public void onExecuted( AjaxRequestTarget target, Change change ) {
                update( target, change );
            }
        } );
        commandWrappers.add( new CommandWrapper( new PasteFlow( getPart() ) ) {
            public void onExecuted( AjaxRequestTarget target, Change change ) {
                update( target, change );
            }
        } );
        commandWrappers.add( new CommandWrapper( new AddUserIssue( getPart() ) ) {
            public void onExecuted( AjaxRequestTarget target, Change change ) {
                update( target, change );
            }
        } );
        commandWrappers.add( new CommandWrapper( new DuplicatePart( getPart() ) ) {
            public void onExecuted( AjaxRequestTarget target, Change change ) {
                update( target, change );
            }
        } );
        commandWrappers.add( new CommandWrapper( new RemovePart( getPart() ) ) {
            public void onExecuted( AjaxRequestTarget target, Change change ) {
                Part part = getPart();
                update( target, change );
                if ( part.getActor() != null )
                    getCommander().cleanup( Actor.class, part.getActor().getName() );
                if ( part.getRole() != null )
                    getCommander().cleanup( Role.class, part.getRole().getName() );
                if ( part.getOrganization() != null )
                    getCommander().cleanup( Organization.class, part.getOrganization().getName() );
                if ( part.getJurisdiction() != null )
                    getCommander().cleanup( Place.class, part.getJurisdiction().getName() );
                if ( part.getLocation() != null )
                    getCommander().cleanup( Place.class, part.getLocation().getName() );
            }
        } );
        return commandWrappers;
    }

    private Part getPart() {
        return (Part) getModel().getObject();
    }

}
