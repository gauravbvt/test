package com.mindalliance.channels.pages.components.menus;

import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.Flow;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.pages.ScenarioPage;
import com.mindalliance.channels.command.commands.AddIssue;
import com.mindalliance.channels.command.commands.BreakUpFlow;
import com.mindalliance.channels.command.commands.DuplicateFlow;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.link.Link;

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

    public FlowActionsMenuPanel( String s, IModel<? extends ModelObject> model, boolean isOutcome, boolean isCollapsed ) {
        super( s, model );
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
        // Show details
        if ( isCollapsed ) {
            ExternalLink showDetailsLink = new ExternalLink( "link", getRequest().getURL() + "&expand=" + getFlow().getId() );
            menuItems.add( new LinkMenuItem( "menuItem", new Model<String>( "Show details" ), showDetailsLink ) );
        }
        else {
            ExternalLink showDetailsLink = new ExternalLink(
                    "link",
                    getRequest().getURL().replaceAll( 
                            "&" + ScenarioPage.EXPAND_PARM + "=" + getFlow().getId(), "" ) );
            menuItems.add( new LinkMenuItem( "menuItem", new Model<String>( "Hide details" ), showDetailsLink ) );
        }
        // Commands
        menuItems.addAll( getCommandMenuItems( "menuItem", getCommandWrappers() ) );
        return menuItems;
    }

    private List<CommandWrapper> getCommandWrappers() {
        return new ArrayList<CommandWrapper>() {
            private final Scenario scenario = getFlow().getScenario();
            {
                add( new CommandWrapper( new AddIssue( getFlow() ) ) {
                    public void onExecution( AjaxRequestTarget target, Object result ) {
                        updateWith( target, getFlow() );
                    }
                } );
                if ( ( isOutcome && getFlow().getTarget().isPart() )
                        || ( !isOutcome && getFlow().getSource().isPart() ) ) {
                    add( new CommandWrapper( new DuplicateFlow( getFlow(), isOutcome ) ) {
                        public void onExecution( AjaxRequestTarget target, Object result ) {
                            updateWith( target, scenario );
                        }
                    } );
                }
                add( new CommandWrapper( new BreakUpFlow( getFlow() ) ) {
                    public void onExecution( AjaxRequestTarget target, Object result ) {
                        updateWith( target, scenario );
                    }
                } );
            }
        };
    }

    private Flow getFlow() {
        return (Flow) getModel().getObject();
    }
}
