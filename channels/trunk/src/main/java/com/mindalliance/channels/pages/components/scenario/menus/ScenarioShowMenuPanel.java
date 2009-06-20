package com.mindalliance.channels.pages.components.scenario.menus;

import com.mindalliance.channels.geo.GeoLocatable;
import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Scenario;
import com.mindalliance.channels.pages.GeoMapPage;
import com.mindalliance.channels.pages.components.menus.LinkMenuItem;
import com.mindalliance.channels.pages.components.menus.MenuPanel;
import com.mindalliance.channels.pages.components.scenario.ScenarioEditPanel;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Scenario show menu.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 8, 2009
 * Time: 12:54:28 PM
 */
public class ScenarioShowMenuPanel extends MenuPanel {

    private ScenarioEditPanel scenarioEditPanel;

    public ScenarioShowMenuPanel( String s, IModel<? extends Identifiable> model, Set<Long> expansions ) {
        super( s, "Show", model, expansions );
    }

    /**
     * {@inheritDoc}
     */
    public List<Component> getMenuItems() {
        List<Component> menuItems = new ArrayList<Component>();
        // Details
        Link detailsLink = new AjaxFallbackLink( "link" ) {
            public void onClick( AjaxRequestTarget target ) {
                changeAspectTo( target, ScenarioEditPanel.DETAILS );
            }
        };
        menuItems.add( new LinkMenuItem(
                "menuItem",
                new Model<String>( "Details" ),
                detailsLink ) );
        // Risks
        Link incidentsLink = new AjaxFallbackLink( "link" ) {
            public void onClick( AjaxRequestTarget target ) {
                changeAspectTo( target, ScenarioEditPanel.RISKS );
            }
        };
        menuItems.add( new LinkMenuItem(
                "menuItem",
                new Model<String>( "Risks" ),
                incidentsLink ) );

        // Map
        List<GeoLocatable> geoLocatables = new ArrayList<GeoLocatable>();
        Iterator<Part> parts = getScenario().parts();
        while ( parts.hasNext() ) {
            geoLocatables.add( parts.next() );
        }
        BookmarkablePageLink<GeoMapPage> geomapLink = GeoMapPage.makeLink(
                "link",
                new Model<String>( "Tasks with known locations in scenario " + getScenario().getName() ),
                geoLocatables );
        if ( geoLocatables.isEmpty() ) {
            geomapLink.setEnabled( false );
        }
        menuItems.add( new LinkMenuItem(
                "menuItem",
                new Model<String>( "Map" ),
                geomapLink ) );

        return menuItems;
    }

    private void changeAspectTo( AjaxRequestTarget target, String aspect ) {
        scenarioEditPanel.setAspectShown( target, aspect );
    }

    public void setScenarioEditPanel( ScenarioEditPanel scenarioEditPanel ) {
        this.scenarioEditPanel = scenarioEditPanel;
    }

    /**
     * Get scenario from model.
     *
     * @return a scenario
     */
    public Scenario getScenario() {
        return (Scenario) getModel().getObject();
    }
}
