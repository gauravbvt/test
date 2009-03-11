package com.mindalliance.channels.pages.components.menus;

import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.commands.AddIssue;
import com.mindalliance.channels.command.commands.AddPart;
import com.mindalliance.channels.command.commands.RemoveScenario;
import com.mindalliance.channels.command.commands.AddScenario;
import com.mindalliance.channels.pages.ExportPage;
import com.mindalliance.channels.pages.ScenarioPage;
import com.mindalliance.channels.pages.Project;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
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

    private WebMarkupContainer menuDiv;
    private Set<Long> expansions;

    public ScenarioActionsMenuPanel( String s, IModel<Scenario> model, Set<Long> expansions ) {
        super( s, model );
        this.expansions = expansions;
        init();
    }

    private void init() {
        menuDiv = new WebMarkupContainer( "menu" );
        menuDiv.setOutputMarkupId( true );
        add( menuDiv );
        ListView<Component> menuItems = new ListView<Component>( "items", new PropertyModel<List<Component>>( this, "menuItems" ) ) {
            protected void populateItem( ListItem<Component> item ) {
                item.add( item.getModelObject() );
            }
        };
        menuDiv.add( menuItems );
    }

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
                            "link", ScenarioPage.class,
                            ((ScenarioPage)getWebPage()).getParametersCollapsing( getScenario().getId() ) );
            menuItems.add( new LinkMenuItem( "menuItem", new Model<String>( "Hide details" ), editLink ) );

        } else {
            editLink =
                    new BookmarkablePageLink<Scenario>(
                            "link", ScenarioPage.class,   // NON-NLS
                            ((ScenarioPage)getWebPage()).getParametersExpanding( getScenario().getId() ) );
            menuItems.add( new LinkMenuItem( "menuItem", new Model<String>( "Show details" ), editLink ) );
        }       
        // Commands
        menuItems.addAll( getCommandMenuItems( "menuItem", getCommandWrappers()));
        return menuItems;
    }

    private List<CommandWrapper> getCommandWrappers() {
        return new ArrayList<CommandWrapper>() {
            {
                add( new CommandWrapper(new AddPart( getScenario())) {
                    public void onExecution(AjaxRequestTarget target, Object result) {
                        updateWith( target );
                    }
                } );
                add( new CommandWrapper(new AddIssue( getScenario())) {
                    public void onExecution(AjaxRequestTarget target, Object result) {
                        updateWith( target );
                    }
                } );
                add( new CommandWrapper(new AddScenario( ) ) {
                     public void onExecution( AjaxRequestTarget target, Object result ) {
                         ((ScenarioPage)getWebPage()).redirectTo( (Scenario)result );
                     }
                 });
                add( new CommandWrapper(new RemoveScenario( getScenario() ) ) {
                    public void onExecution( AjaxRequestTarget target, Object result ) {
                        ((ScenarioPage)getWebPage()).redirectTo( 
                                Project.getProject().getService().getDefaultScenario() );
                    }
                });
             }
        };
    }


    private Scenario getScenario() {
        return (Scenario) getModel().getObject();
    }

}
