package com.mindalliance.channels.pages.components.menus;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.dao.PlanManager;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.model.Scenario;
import com.mindalliance.channels.model.User;
import com.mindalliance.channels.pages.AdminPage;
import com.mindalliance.channels.pages.reports.PlanReportPage;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Pages menu for  a scenario
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 10, 2009
 * Time: 8:30:46 PM
 */
public class PlanShowMenuPanel extends MenuPanel {

    /**
     * Plan manager.
     */
    @SpringBean
    private PlanManager planManager;

    public PlanShowMenuPanel( String s, IModel<? extends Scenario> model, Set<Long> expansions ) {
        super( s, "Show", model, expansions );
    }

    /**
     * {@inheritDoc
     */
    @Override
    @SuppressWarnings( "unchecked" )
    public List<Component> getMenuItems() {
        List<Component> menuItems = new ArrayList<Component>();
        final Plan plan = planManager.getCurrentPlan();
        // Edit<->Hide
        if ( getExpansions().contains( plan.getId() ) ) {
            AjaxFallbackLink planMapLink = new AjaxFallbackLink( "link" ) {
                @Override
                public void onClick( AjaxRequestTarget target ) {
                    update( target, new Change( Change.Type.Collapsed, plan ) );
                }
            };
            menuItems.add( new LinkMenuItem(
                    "menuItem",
                    new Model<String>( "Hide about plan" ),
                    planMapLink ) );
        } else {
            AjaxFallbackLink planMapLink = new AjaxFallbackLink( "link" ) {
                @Override
                public void onClick( AjaxRequestTarget target ) {
                    update( target, new Change( Change.Type.Expanded, plan ) );
                }
            };
            menuItems.add( new LinkMenuItem(
                    "menuItem",
                    new Model<String>( "About plan" ),
                    planMapLink ) );
        }

        Link editLink;
        if ( getExpansions().contains( getScenario().getId() ) ) {
            editLink =
                    new AjaxFallbackLink( "link" ) {
                        @Override
                        public void onClick( AjaxRequestTarget target ) {
                            update( target, new Change( Change.Type.Collapsed, getScenario() ) );
                        }
                    };
            menuItems.add( new LinkMenuItem(
                    "menuItem",
                    new Model<String>( "Hide about scenario" ),
                    editLink ) );

        } else {
            editLink =
                    new AjaxFallbackLink( "link" ) {
                        @Override
                        public void onClick( AjaxRequestTarget target ) {
                            update( target, new Change( Change.Type.Expanded, getScenario() ) );
                        }
                    };
            menuItems.add( new LinkMenuItem(
                    "menuItem",
                    new Model<String>( "About scenario" ),
                    editLink ) );
        }
        BookmarkablePageLink reportLink = new BookmarkablePageLink( "link", PlanReportPage.class );
        reportLink.add( new AttributeModifier( "target", true, new Model<String>( "report" ) ) );
//        reportLink.setPopupSettings( new PopupSettings(
//                PopupSettings.RESIZABLE |
//                        PopupSettings.SCROLLBARS |
//                        PopupSettings.MENU_BAR ) );
        menuItems.add( new LinkMenuItem(
                "menuItem",
                new Model<String>( "Plan as document" ),
                reportLink ) );

        if ( User.current().isAdmin() ) {
            BookmarkablePageLink adminLink = new BookmarkablePageLink( "link", AdminPage.class );
            menuItems.add( new LinkMenuItem(
                    "menuItem",
                    new Model<String>( "Admin page" ),
                    adminLink ) );
        }
        return menuItems;
    }

    private Scenario getScenario() {
        return (Scenario) getModel().getObject();
    }

}
