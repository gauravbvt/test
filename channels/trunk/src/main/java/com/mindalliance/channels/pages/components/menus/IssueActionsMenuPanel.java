package com.mindalliance.channels.pages.components.menus;

import com.mindalliance.channels.Issue;
import com.mindalliance.channels.UserIssue;
import com.mindalliance.channels.command.commands.RemoveIssue;
import com.mindalliance.channels.command.Change;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

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
        super( s, model );
        this.isCollapsed = isCollapsed;
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
        // Commands
        menuItems.addAll( getCommandMenuItems( "menuItem", getCommandWrappers() ) );
        return menuItems;
    }

    private Issue getIssue() {
        return (Issue) getModel().getObject();
    }

    private List<CommandWrapper> getCommandWrappers() {
        return new ArrayList<CommandWrapper>() {
            {
                final Issue issue = getIssue();
                if ( !issue.isDetected() )
                    add( new CommandWrapper( new RemoveIssue( (UserIssue) issue ) ) {
                        public void onExecuted( AjaxRequestTarget target, Change change ) {
                            update( target, change );
                        }
                    } );
            }
        };
    }


}
