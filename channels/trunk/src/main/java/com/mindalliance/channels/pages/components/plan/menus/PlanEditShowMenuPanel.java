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
public class PlanEditShowMenuPanel  extends MenuPanel {


    private PlanEditPanel planEditPanel;

    public PlanEditShowMenuPanel( String id, IModel<? extends Identifiable> model ) {
        super( id, "Show", model, null );
    }

    public List<Component> getMenuItems() {
        List<Component> menuItems = new ArrayList<Component>();
        // Details
        Link detailsLink = new AjaxFallbackLink("link"){
            public void onClick( AjaxRequestTarget target ) {
                changeAspectTo(target, PlanEditPanel.DETAILS);
            }
        };
        menuItems.add( new LinkMenuItem(
                "menuItem",
                new Model<String>( "Details" ),
                detailsLink ) );
        // Incidents
        Link incidentsLink = new AjaxFallbackLink("link"){
             public void onClick( AjaxRequestTarget target ) {
                 changeAspectTo(target, PlanEditPanel.INCIDENTS);
             }
         };
        menuItems.add( new LinkMenuItem(
                "menuItem",
                new Model<String>( "Incidents" ),
                incidentsLink ) );
        // Map
        Link mapLink = new AjaxFallbackLink("link"){
             public void onClick( AjaxRequestTarget target ) {
                 changeAspectTo(target, PlanEditPanel.MAP);
             }
         };
         menuItems.add( new LinkMenuItem(
                 "menuItem",
                 new Model<String>( "Map" ),
                 mapLink ) );
        return menuItems;
    }

    private void changeAspectTo(AjaxRequestTarget target, String aspect) {
        planEditPanel.setAspectShown( target, aspect );
    }

    public void setPlanEditPanel( PlanEditPanel planEditPanel ) {
        this.planEditPanel = planEditPanel;
    }
}
