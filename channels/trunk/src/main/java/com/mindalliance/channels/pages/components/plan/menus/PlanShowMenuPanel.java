package com.mindalliance.channels.pages.components.plan.menus;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.dao.PlanManager;
import com.mindalliance.channels.dao.User;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.pages.AdminPage;
import com.mindalliance.channels.pages.components.menus.LinkMenuItem;
import com.mindalliance.channels.pages.components.menus.MenuPanel;
import com.mindalliance.channels.pages.components.plan.PlanEditPanel;
import com.mindalliance.channels.pages.reports.SOPsReportPage;
import com.mindalliance.channels.surveys.Survey;
import com.mindalliance.channels.surveys.SurveyService;
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
 * Pages menu for  a segment
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
    /**
     * Survey service.
     */
    @SpringBean
    private SurveyService surveyService;

    public PlanShowMenuPanel( String s, IModel<? extends Segment> model, Set<Long> expansions ) {
        super( s, "Show", model, expansions );
    }

    /**
     * {@inheritDoc
     */
    @Override
    @SuppressWarnings( "unchecked" )
    public List<Component> getMenuItems() {
        List<Component> menuItems = new ArrayList<Component>();
        final Plan plan = User.plan();
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
        if ( getExpansions().contains( getSegment().getId() ) ) {
            editLink =
                    new AjaxFallbackLink( "link" ) {
                        @Override
                        public void onClick( AjaxRequestTarget target ) {
                            update( target, new Change( Change.Type.Collapsed, getSegment() ) );
                        }
                    };
            menuItems.add( new LinkMenuItem(
                    "menuItem",
                    new Model<String>( "Hide about plan segment" ),
                    editLink ) );

        } else {
            editLink =
                    new AjaxFallbackLink( "link" ) {
                        @Override
                        public void onClick( AjaxRequestTarget target ) {
                            update( target, new Change( Change.Type.Expanded, getSegment() ) );
                        }
                    };
            menuItems.add( new LinkMenuItem(
                    "menuItem",
                    new Model<String>( "About plan segment" ),
                    editLink ) );
        }
        if ( getPlan().isDevelopment() ) {
            Link surveyLink;
            if ( getExpansions().contains( Survey.UNKNOWN.getId() ) ) {
                surveyLink =
                        new AjaxFallbackLink( "link" ) {
                            @Override
                            public void onClick( AjaxRequestTarget target ) {
                                update( target, new Change( Change.Type.Collapsed, Survey.UNKNOWN ) );
                            }
                        };
                menuItems.add( new LinkMenuItem(
                        "menuItem",
                        new Model<String>( "Hide surveys" ),
                        surveyLink ) );

            } else {
                surveyLink =
                        new AjaxFallbackLink( "link" ) {
                            @Override
                            public void onClick( AjaxRequestTarget target ) {
                                update( target, new Change( Change.Type.Expanded, Survey.UNKNOWN ) );
                            }
                        };
                menuItems.add( new LinkMenuItem(
                        "menuItem",
                        new Model<String>( "Surveys" ),
                        surveyLink ) );
            }
        }
        BookmarkablePageLink reportLink = new BookmarkablePageLink( "link", SOPsReportPage.class );
        reportLink.add( new AttributeModifier( "target", true, new Model<String>( "_blank" ) ) );
        /*reportLink.setPopupSettings( new PopupSettings(
               PopupSettings.RESIZABLE |
                        PopupSettings.SCROLLBARS |
                        PopupSettings.MENU_BAR ) );*/
        menuItems.add( new LinkMenuItem(
                "menuItem",
                new Model<String>( "Info Sharing Procedures" ),
                reportLink ) );

/*
        BookmarkablePageLink playbooksLink = new BookmarkablePageLink<SOPsReportPage>( "link", TaskPlaybook.class );
        playbooksLink.setPopupSettings( new PopupSettings(
               PopupSettings.RESIZABLE |
                        PopupSettings.SCROLLBARS |
                        PopupSettings.MENU_BAR ) );
        menuItems.add( new LinkMenuItem(
                "menuItem",
                new Model<String>( "Playbooks" ),
                playbooksLink ) );
*/

        if ( User.current().isAdmin() ) {
            BookmarkablePageLink adminLink = new BookmarkablePageLink( "link", AdminPage.class );
            menuItems.add( new LinkMenuItem(
                    "menuItem",
                    new Model<String>( "Admin page" ),
                    adminLink ) );
        }
        // Index
        Link indexLink = new AjaxFallbackLink( "link" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Expanded, plan, PlanEditPanel.INDEX ) );
            }
        };
        menuItems.add( new LinkMenuItem(
                "menuItem",
                new Model<String>( "Index" ),
                indexLink ) );
        
        return menuItems;
    }

    private Segment getSegment() {
        return (Segment) getModel().getObject();
    }

}
