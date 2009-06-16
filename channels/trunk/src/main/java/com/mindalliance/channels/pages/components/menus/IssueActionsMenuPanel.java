package com.mindalliance.channels.pages.components.menus;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.commands.PasteAttachment;
import com.mindalliance.channels.command.commands.RemoveIssue;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.UserIssue;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 12, 2009
 * Time: 1:52:04 PM
 */
public class IssueActionsMenuPanel extends MenuPanel {
    /**
     * Whether the issue is shown collapsed.
     */
    private boolean isCollapsed;

    public IssueActionsMenuPanel( String s, IModel<? extends Issue> model, boolean isCollapsed ) {
        super( s, "Menu", model, null );
        this.isCollapsed = isCollapsed;
        doInit();
    }

    /**
     * {@inheritDoc}
     */
    protected void init() {
        // do nothing
    }

    private void doInit() {
        super.init();
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
        // Show/hide details
        if ( isCollapsed ) {
            AjaxFallbackLink showLink = new AjaxFallbackLink( "link" ) {
                public void onClick( AjaxRequestTarget target ) {
                    update( target, new Change( Change.Type.Expanded, getIssue() ) );
                }
            };
            menuItems.add( new LinkMenuItem(
                    "menuItem",
                    new Model<String>( "Show details" ),
                    showLink ) );
        } else {
            AjaxFallbackLink hideLink = new AjaxFallbackLink( "link" ) {
                public void onClick( AjaxRequestTarget target ) {
                    update( target, new Change( Change.Type.Collapsed, getIssue() ) );
                }
            };
            menuItems.add( new LinkMenuItem(
                    "menuItem",
                    new Model<String>( "Hide details" ),
                    hideLink ) );
        }
        // Undo and redo
        // Undo and redo
        menuItems.add( this.getUndoMenuItem( "menuItem" ) );
        menuItems.add( this.getRedoMenuItem( "menuItem" ) );
        // Commands
        String disablement =
                isLockedByUser( getIssue() )
                        ? null
                        : ( getCommander().isTimedOut() || !isCollapsed && getLockOwner( getIssue() ) == null )
                        ? "Timed out"
                        : ( "(Edited by " + getLockOwner( getIssue() ) + ")" );
        if ( disablement == null ) {
            // Commands
            menuItems.addAll( getCommandMenuItems( "menuItem", getCommandWrappers() ) );
        } else {
            // Commands disabled
            Label label = new Label( "menuItem", disablement );
            label.add( new AttributeModifier( "class", true, new Model<String>( "disabled" ) ) );
            menuItems.add( label );
        }
        return menuItems;
    }

    private Issue getIssue() {
        return (Issue) getModel().getObject();
    }

    private List<CommandWrapper> getCommandWrappers() {
        List<CommandWrapper> commandWrappers = new ArrayList<CommandWrapper>();
        Issue issue = getIssue();
        if ( !issue.isDetected() ) {
            commandWrappers.add( new CommandWrapper( new RemoveIssue( (UserIssue) issue ) ) {
                public void onExecuted( AjaxRequestTarget target, Change change ) {
                    update( target, change );
                }
            } );
            if ( !isCollapsed )
                commandWrappers.add( new CommandWrapper( new PasteAttachment( (UserIssue) issue ) ) {
                    public void onExecuted( AjaxRequestTarget target, Change change ) {
                        update( target, change );
                    }
                } );
        }
        return commandWrappers;
    }


}
