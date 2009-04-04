package com.mindalliance.channels.pages.components.menus;

import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.pages.Project;
import com.mindalliance.channels.pages.ProjectPage;
import com.mindalliance.channels.command.Change;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.ajax.AjaxRequestTarget;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;

/**
 * Pages menu for  a scenario
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 10, 2009
 * Time: 8:30:46 PM
 */
public class ProjectShowMenuPanel extends MenuPanel {

    public ProjectShowMenuPanel( String s, IModel<? extends Scenario> model, Set<Long> expansions ) {
        super( s, model, expansions );
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

    public List<Component> getMenuItems() {
        List<Component> menuItems = new ArrayList<Component>();
        ExternalLink reportLink = new ExternalLink( "link", "report.html" );
        menuItems.add( new LinkMenuItem(
                "menuItem",
                new Model<String>( "Plan Playbook" ),
                reportLink ) );
        if ( getExpansions().contains( Project.getProject().getId() ) ) {
            AjaxFallbackLink planMapLink = new AjaxFallbackLink( "link" ) {
                public void onClick( AjaxRequestTarget target ) {
                    update( target, new Change( Change.Type.Collapsed, Project.getProject() ) );
                }
            };
            menuItems.add( new LinkMenuItem(
                    "menuItem",
                    new Model<String>( "Hide plan map" ),
                    planMapLink ) );
        } else {
            AjaxFallbackLink planMapLink = new AjaxFallbackLink( "link" ) {
                public void onClick( AjaxRequestTarget target ) {
                    update( target, new Change( Change.Type.Expanded, Project.getProject() ) );
                }
            };
            menuItems.add( new LinkMenuItem(
                    "menuItem",
                    new Model<String>( "Show plan map" ),
                    planMapLink ) );
        }
        return menuItems;
    }


}
