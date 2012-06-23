package com.mindalliance.channels.pages.components.menus;

import com.mindalliance.channels.core.model.Identifiable;
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
public abstract class ActionMenuPanel extends MenuPanel {

    protected ActionMenuPanel( String s, IModel<? extends Identifiable> model, Set<Long> expansions ) {
        super( s, "Actions", model, expansions );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<LinkMenuItem> getMenuItems() {
        synchronized ( getCommander() ) {
            List<LinkMenuItem> menuItems = new ArrayList<LinkMenuItem>( );
            menuItems.add(  getSendMessageMenuItem( "menuItem" ) );
            if ( getPlan().isDevelopment() ) {
                menuItems.add( getUndoMenuItem( "menuItem" ) );
                menuItems.add( getRedoMenuItem( "menuItem" ) );
            }
            // Commands
            menuItems.addAll( getCommandMenuItems( "menuItem", getCommandWrappers() ) );
            return menuItems;
        }
    }

    /**
     * Get command wrappers from which menu items will be constructed.
     *
     * @return a list of command wrappers
     */
    protected abstract List<CommandWrapper> getCommandWrappers();

    protected boolean isLockable( ) {
        return getLockManager().isLockableByUser( getUser().getUsername(), getIdentifiable() );
    }

    protected Identifiable getIdentifiable() {
        return getModel().getObject();
    }
}
