package com.mindalliance.channels.pages.components.menus;

import com.mindalliance.channels.Flow;
import com.mindalliance.channels.command.commands.AddUserIssue;
import com.mindalliance.channels.command.commands.BreakUpFlow;
import com.mindalliance.channels.command.commands.DuplicateFlow;
import com.mindalliance.channels.command.Change;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.list.ListItem;

import java.util.List;
import java.util.ArrayList;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 11, 2009
 * Time: 4:35:49 PM
 */
public class FlowActionsMenuPanel extends MenuPanel {

    private boolean isOutcome;
    private boolean isCollapsed;

    public FlowActionsMenuPanel( String s, IModel<? extends Flow> model, boolean isOutcome, boolean isCollapsed ) {
        super( s, model, null );
        this.isOutcome = isOutcome;
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
                    update( target, new Change( Change.Type.Expanded, getFlow() ) );
                }
            };
            menuItems.add( new LinkMenuItem( "menuItem", new Model<String>( "Show details" ), showLink ) );
        } else {
            AjaxFallbackLink hideLink = new AjaxFallbackLink( "link" ) {
                public void onClick( AjaxRequestTarget target ) {
                    update( target, new Change( Change.Type.Collapsed, getFlow() ) );
                }
            };
            menuItems.add( new LinkMenuItem( "menuItem", new Model<String>( "Hide details" ), hideLink ) );
        }
        // Commands
        menuItems.addAll( getCommandMenuItems( "menuItem", getCommandWrappers() ) );
        return menuItems;
    }

    private List<CommandWrapper> getCommandWrappers() {
        return new ArrayList<CommandWrapper>() {
            {
                final Flow flow = getFlow();
                if ( !isCollapsed )
                    add( new CommandWrapper( new AddUserIssue( flow ) ) {
                        public void onExecuted( AjaxRequestTarget target, Change change ) {
                            update( target, change );
                        }
                    } );
                if ( ( isOutcome && getFlow().getTarget().isPart() )
                        || ( !isOutcome && getFlow().getSource().isPart() ) ) {
                    add( new CommandWrapper( new DuplicateFlow( flow, isOutcome ) ) {
                        public void onExecuted( AjaxRequestTarget target, Change change ) {
                            update( target, change );
                        }
                    } );
                }
                add( new CommandWrapper( new BreakUpFlow( flow ) ) {
                    public void onExecuted( AjaxRequestTarget target, Change change ) {
                        update( target, change );
                    }
                } );
            }
        };
    }

    private Flow getFlow() {
        return (Flow) getModel().getObject();
    }
}
