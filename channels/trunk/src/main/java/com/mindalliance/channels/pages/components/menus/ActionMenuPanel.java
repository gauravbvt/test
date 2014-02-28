package com.mindalliance.channels.pages.components.menus;

import com.mindalliance.channels.core.community.CommunityService;
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
        List<LinkMenuItem> menuItems = new ArrayList<LinkMenuItem>( );
        synchronized ( getCommander() ) {
            addLockedOutMenuItem( menuItems, "menuItem" );
            menuItems.add(  getSendMessageMenuItem( "menuItem" ) );
            if ( getCollaborationModel().isDevelopment() ) {
                menuItems.add( getUndoMenuItem( "menuItem" ) );
                menuItems.add( getRedoMenuItem( "menuItem" ) );
            }
            // Commands
            menuItems.addAll( getCommandMenuItems( "menuItem", getCommandWrappers( getCommunityService() ) ) );
         }
        return menuItems;
    }

    private void addLockedOutMenuItem( List<LinkMenuItem> menuItems, String id ) {
        Identifiable identifiable = getIdentifiable();
        if ( !isLockedByUser( identifiable ) && getLockOwner( identifiable ) != null ) {
            menuItems.add( editedByLinkMenuItem( id, identifiable, getLockOwner( identifiable ) ) );
        }
    }

    /**
     * Get command wrappers from which menu items will be constructed.
     * @param communityService a community service
     * @return a list of command wrappers
     */
    protected abstract List<CommandWrapper> getCommandWrappers( CommunityService communityService );

    protected boolean isLockable( CommunityService communityService ) {
        return getLockManager().isLockableByUser( getUser().getUsername(), getIdentifiable() );
    }

    protected Identifiable getIdentifiable() {
        return getModel().getObject();
    }
}
