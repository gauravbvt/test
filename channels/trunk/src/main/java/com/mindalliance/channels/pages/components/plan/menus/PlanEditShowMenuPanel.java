package com.mindalliance.channels.pages.components.plan.menus;

import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.pages.components.menus.LinkMenuItem;
import com.mindalliance.channels.pages.components.menus.MenuPanel;
import com.mindalliance.channels.pages.components.plan.PlanEditPanel;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Plan edit show menu.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 7, 2009
 * Time: 4:54:57 AM
 */
public class PlanEditShowMenuPanel extends MenuPanel {

    /**
     * Plan edit panel with this menu.
     */
    private PlanEditPanel planEditPanel;

    public PlanEditShowMenuPanel( String id, IModel<? extends Identifiable> model ) {
        super( id, "Show", model, null );
    }

    public List<Component> getMenuItems() {
        List<Component> menuItems = new ArrayList<Component>();
        // Details
        Link detailsLink = new AjaxFallbackLink( "link" ) {
            public void onClick( AjaxRequestTarget target ) {
                changeAspectTo( target, PlanEditPanel.DETAILS );
            }
        };
        menuItems.add( new LinkMenuItem(
                "menuItem",
                new Model<String>( "Details" ),
                detailsLink ) );
        // Incidents
        Link incidentsLink = new AjaxFallbackLink( "link" ) {
            public void onClick( AjaxRequestTarget target ) {
                changeAspectTo( target, PlanEditPanel.EVENTS );
            }
        };
        menuItems.add( new LinkMenuItem(
                "menuItem",
                new Model<String>( "All events" ),
                incidentsLink ) );
        // Secrecy classifications
        Link classificationsLink = new AjaxFallbackLink( "link" ) {
            public void onClick( AjaxRequestTarget target ) {
                changeAspectTo( target, PlanEditPanel.CLASSIFICATIONS );
            }
        };
        menuItems.add( new LinkMenuItem(
                "menuItem",
                new Model<String>( "Secrecy classifications" ),
                classificationsLink ) );
        // Organizations in scope
        Link scopeLink = new AjaxFallbackLink( "link" ) {
            public void onClick( AjaxRequestTarget target ) {
                changeAspectTo( target, PlanEditPanel.SCOPE );
            }
        };
        menuItems.add( new LinkMenuItem(
                "menuItem",
                new Model<String>( "All organizations" ),
                scopeLink ) );
        // Map
        Link mapLink = new AjaxFallbackLink( "link" ) {
            public void onClick( AjaxRequestTarget target ) {
                changeAspectTo( target, PlanEditPanel.MAP );
            }
        };
        menuItems.add( new LinkMenuItem(
                "menuItem",
                new Model<String>( "All plan segments" ),
                mapLink ) );
        // Who's who
        Link whoswhoLink = new AjaxFallbackLink( "link" ) {
            public void onClick( AjaxRequestTarget target ) {
                changeAspectTo( target, PlanEditPanel.WHOSWHO );
            }
        };
        menuItems.add( new LinkMenuItem(
                "menuItem",
                new Model<String>( "Who's who" ),
                whoswhoLink ) );
        // All issues
        Link issuesLink = new AjaxFallbackLink( "link" ) {
            public void onClick( AjaxRequestTarget target ) {
                changeAspectTo( target, PlanEditPanel.ISSUES );
            }
        };
        menuItems.add( new LinkMenuItem(
                "menuItem",
                new Model<String>( "All issues" ),
                issuesLink ) );
        // Bibliography
        Link biblioLink = new AjaxFallbackLink( "link" ) {
            public void onClick( AjaxRequestTarget target ) {
                changeAspectTo( target, PlanEditPanel.BIBLIOGRAPHY );
            }
        };
        menuItems.add( new LinkMenuItem(
                "menuItem",
                new Model<String>( "Bibliography" ),
                biblioLink ) );
        // Index
        Link indexLink = new AjaxFallbackLink( "link" ) {
            public void onClick( AjaxRequestTarget target ) {
                changeAspectTo( target, PlanEditPanel.INDEX );
            }
        };
        menuItems.add( new LinkMenuItem(
                "menuItem",
                new Model<String>( "Index" ),
                indexLink ) );
        // Evaluation
        Link evalLink = new AjaxFallbackLink( "link" ) {
            public void onClick( AjaxRequestTarget target ) {
                changeAspectTo( target, PlanEditPanel.EVAL );
            }
        };
        menuItems.add( new LinkMenuItem(
                "menuItem",
                new Model<String>( "Evaluation" ),
                evalLink ) );
        // Versions
        Link versionsLink = new AjaxFallbackLink( "link" ) {
            public void onClick( AjaxRequestTarget target ) {
                changeAspectTo( target, PlanEditPanel.VERSIONS );
            }
        };
        menuItems.add( new LinkMenuItem(
                "menuItem",
                new Model<String>( "Versions" ),
                versionsLink ) );

        return menuItems;
    }

    private void changeAspectTo( AjaxRequestTarget target, String aspect ) {
        planEditPanel.setAspectShown( target, aspect );
    }

    public void setPlanEditPanel( PlanEditPanel planEditPanel ) {
        this.planEditPanel = planEditPanel;
    }
}
