package com.mindalliance.channels.pages.components.menus;

import com.mindalliance.channels.command.CommandException;
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
     * {@inheritDoc}
     */
    public List<Component> getMenuItems() throws CommandException {
        List<Component> menuItems = new ArrayList<Component>();
        // Undo and redo
        menuItems.add( getUndoMenuItem( "menuItem" ) );
        menuItems.add( getRedoMenuItem( "menuItem" ) );
        menuItems.add( getSendMessageMenuItem( "menuItem" ) );
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
