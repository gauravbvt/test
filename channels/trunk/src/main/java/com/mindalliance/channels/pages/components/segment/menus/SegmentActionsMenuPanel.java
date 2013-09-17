package com.mindalliance.channels.pages.components.segment.menus;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.commands.AddUserIssue;
import com.mindalliance.channels.core.command.commands.PasteAttachment;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.pages.Channels;
import com.mindalliance.channels.pages.components.menus.ActionMenuPanel;
import com.mindalliance.channels.pages.components.menus.CommandWrapper;
import com.mindalliance.channels.pages.components.menus.LinkMenuItem;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Segment edit actions menu.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 8, 2009
 * Time: 12:57:58 PM
 */
public class SegmentActionsMenuPanel extends ActionMenuPanel {

    public SegmentActionsMenuPanel( String s, IModel<? extends Identifiable> model, Set<Long> expansions ) {
        super( s, model, expansions );
    }

    @Override
    public String getHelpTopicId() {
        return "actions-segment";
    }

    @Override
    public List<LinkMenuItem> getMenuItems() {
        List<LinkMenuItem> menuItems = super.getMenuItems();
        // Task mover
        if ( isPlanner() )
            menuItems.add( collapsible( Channels.TASK_MOVER, "Hide task mover", "Move tasks..." ) );
        // Locked
        Segment segment = getSegment();
        if ( !isLockedByUser( segment ) && getLockOwner( segment ) != null ) {
            menuItems.add( editedByLinkMenuItem( "menuItem", segment, getLockOwner( segment ) ) );
        }
        return menuItems;
    }

    /**
     * {@inheritDoc}
     */
    protected List<CommandWrapper> getCommandWrappers() {
        List<CommandWrapper> commandWrappers = new ArrayList<CommandWrapper>();
        if ( isLockable() ) {
            commandWrappers.add( new CommandWrapper( new PasteAttachment( getUser().getUsername(), getSegment() ) ) {
                public void onExecuted( AjaxRequestTarget target, Change change ) {
                    update( target, change );
                }
            } );
            commandWrappers.add( new CommandWrapper( new AddUserIssue( getUser().getUsername(), getSegment() ) ) {
                public void onExecuted(
                        AjaxRequestTarget target,
                        Change change ) {
                    update( target, change );
                }
            } );
        }
        return commandWrappers;
    }

    private Segment getSegment() {
        return (Segment) getModel().getObject();
    }
}
