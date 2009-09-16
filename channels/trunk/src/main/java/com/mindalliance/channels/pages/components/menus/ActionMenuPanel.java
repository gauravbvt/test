package com.mindalliance.channels.pages.components.menus;

import com.mindalliance.channels.model.Identifiable;
import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 21, 2009
 * Time: 12:40:47 PM
 */
abstract public class ActionMenuPanel extends MenuPanel {

    protected ActionMenuPanel( String s, IModel<? extends Identifiable> model, Set<Long> expansions ) {
        super( s, "Actions", model, expansions );
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

    /**
     * Get command wrappers from which menu items will be constructed.
     *
     * @return a list of command wrappers
     */
    protected abstract List<CommandWrapper> getCommandWrappers();


}
