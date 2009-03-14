package com.mindalliance.channels.pages.components.menus;

import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.command.commands.AddUserIssue;
import com.mindalliance.channels.command.commands.AddPart;
import com.mindalliance.channels.command.commands.AddScenario;
import com.mindalliance.channels.command.commands.RemoveScenario;
import com.mindalliance.channels.pages.ExportPage;
import com.mindalliance.channels.pages.Project;
import com.mindalliance.channels.pages.ProjectPage;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Scenario menu.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 10, 2009
 * Time: 8:58:39 AM
 */
public class ScenarioActionsMenuPanel extends MenuPanel {

    /**
     * IDs of expanded model objects.
     */
    private Set<Long> expansions;

    public ScenarioActionsMenuPanel( String s, IModel<? extends Scenario> model, Set<Long> expansions ) {
        super( s, model );
        this.expansions = expansions;
        init();
    }

    private void init() {
        ListView<Component> menuItems = new ListView<Component>( "items", new PropertyModel<List<Component>>( this, "menuItems" ) ) {
            protected void populateItem( ListItem<Component> item ) {
                item.add( item.getModelObject() );
            }
        };
        add( menuItems );
    }

    /**
     * Get population of menu items.
     * @return a list of menu items
     */
    public List<Component> getMenuItems() {
        List<Component> menuItems = new ArrayList<Component>();
        // Undo and redo
        menuItems.add( this.getUndoMenuItem( "menuItem" ) );
        menuItems.add( this.getRedoMenuItem( "menuItem" ) );
        // Edit<->Hide
        Link editLink;
        if ( expansions.contains( getScenario().getId() ) ) {
            editLink =
                    new BookmarkablePageLink<Scenario>(
                            "link", ProjectPage.class,
                            ( (ProjectPage) getWebPage() ).getParametersCollapsing( getScenario().getId() ) );
            menuItems.add( new LinkMenuItem( "menuItem", new Model<String>( "Hide details" ), editLink ) );

        } else {
            editLink =
                    new BookmarkablePageLink<Scenario>(
                            "link", ProjectPage.class,   // NON-NLS
                            ( (ProjectPage) getWebPage() ).getParametersExpanding( getScenario().getId() ) );
            menuItems.add( new LinkMenuItem( "menuItem", new Model<String>( "Show details" ), editLink ) );
        }
        // Commands
        menuItems.addAll( getCommandMenuItems( "menuItem", getCommandWrappers() ) );
        // Export
        menuItems.add( new LinkMenuItem( "menuItem", new Model<String>( "Export to XML" ),
                new BookmarkablePageLink<Scenario>(
                        "link",
                        ExportPage.class,
                        ProjectPage.getParameters( (Scenario) getModel().getObject(), null ) ) ) );

        return menuItems;
    }

    private List<CommandWrapper> getCommandWrappers() {
        return new ArrayList<CommandWrapper>() {
            {
                add( new CommandWrapper( new AddPart( getScenario() ) ) {
                    public void onExecution( AjaxRequestTarget target, Object result ) {
                        updateWith( target, getScenario() );
                    }
                } );
                add( new CommandWrapper( new AddUserIssue( getScenario() ) ) {
                    public void onExecution( AjaxRequestTarget target, Object result ) {
                        updateWith( target, result );
                    }
                } );
                add( new CommandWrapper( new AddScenario() ) {
                    public void onExecution( AjaxRequestTarget target, Object result ) {
                        ( (ProjectPage) getWebPage() ).redirectTo( (Scenario) result );
                    }
                } );
                 add( new CommandWrapper( new RemoveScenario( getScenario() ) ) {
                    public void onExecution( AjaxRequestTarget target, Object result ) {
                        ( (ProjectPage) getWebPage() ).redirectTo(
                                Project.getProject().getService().getDefaultScenario() );
                    }
                } );
            }
        };
    }


    private Scenario getScenario() {
        return (Scenario) getModel().getObject();
    }

}
