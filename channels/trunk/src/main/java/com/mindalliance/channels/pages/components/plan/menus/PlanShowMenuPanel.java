package com.mindalliance.channels.pages.components.plan.menus;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.dao.PlanManager;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.pages.AbstractChannelsWebPage;
import com.mindalliance.channels.pages.Channels;
import com.mindalliance.channels.pages.HelpPage;
import com.mindalliance.channels.pages.components.menus.LinkMenuItem;
import com.mindalliance.channels.pages.components.menus.MenuPanel;
import com.mindalliance.channels.pages.components.plan.PlanEditPanel;
import com.mindalliance.channels.social.model.Feedback;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.link.PopupSettings;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Pages menu for  a segment.
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
 /*   @SpringBean
    private SurveyService surveyService;
*/
    public PlanShowMenuPanel( String s, IModel<? extends Segment> model, Set<Long> expansions ) {
        super( s, "Show", model, expansions );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Component> getMenuItems() {
        synchronized ( getCommander() ) {
            Plan plan = getPlan();
            List<Component> menuItems = new ArrayList<Component>();

            menuItems.addAll(
                    Arrays.asList(
                            collapsible( Channels.SOCIAL_ID, "Hide planners", "Planners" ),
                            collapsible( plan, "Hide about plan", "About plan" ),
                            collapsible(
                                    getSegment(), "Hide about plan segment", "About plan segment" ) ) );

/*
            if ( plan.isDevelopment() )
                menuItems.add( collapsible( Survey.UNKNOWN, "Hide surveys", "Surveys" ) );
*/
            menuItems.add( collapsible( Feedback.UNKNOWN, "Hide all feedback", "All feedback" ) );
      //      menuItems.add( collapsible( RFISurvey.UNKNOWN, "Hide surveys", "Surveys" ) );
            menuItems.addAll(
                    Arrays.asList(
                            newLink( "All segments", plan, PlanEditPanel.MAP ),
                            newLink( "All issues", plan, PlanEditPanel.ISSUES ),
                            newLink( "All types", plan, PlanEditPanel.TYPOLOGIES ),
                            newLink( "Index", plan, PlanEditPanel.INDEX ),
                            newLink( "Assignments & Commitments", plan, PlanEditPanel.PROCEDURES ),
                            newLink( "Requirements", plan, PlanEditPanel.REQUIREMENTS ),
                            newLink( "Participations", plan, PlanEditPanel.PARTICIPATIONS ),
                            newLink(
                                    "Help", newTargetedLink(
                                    "help", HelpPage.class, new PopupSettings(
                                            PopupSettings.RESIZABLE | PopupSettings.SCROLLBARS | PopupSettings.MENU_BAR
                                                    | PopupSettings.TOOL_BAR ) ) ) ) );

            return menuItems;
        }
    }

    private LinkMenuItem newLink( String title, final Plan plan, final String action ) {
        return newLink(
                title,
                new AjaxFallbackLink( "link" ) {
                    @Override
                    public void onClick( AjaxRequestTarget target ) {
                        update( target, new Change( Change.Type.Expanded, plan, action ) );
                    }
                } );
    }

    private static LinkMenuItem newLink( String title, Link<?> link ) {
        return new LinkMenuItem(
                "menuItem",
                new Model<String>( title ),
                link );
    }

    private <T extends WebPage> BookmarkablePageLink<T> newTargetedLink(
            String target, Class<T> pageClass, PopupSettings popupSettings ) {
        return AbstractChannelsWebPage.newTargetedLink( "link", target, pageClass, popupSettings, getPlan() );
    }

    private LinkMenuItem collapsible( final long id,
                                      String expandedTitle, String collapsedTitle ) {

        return new LinkMenuItem( "menuItem",
                new Model<String>( isExpanded( id ) ? expandedTitle : collapsedTitle ),
                new AjaxFallbackLink( "link" ) {
                    @Override
                    public void onClick( AjaxRequestTarget target ) {
                        final boolean expanded = isExpanded( id );
                        update( target,
                                new Change( expanded ? Change.Type.Collapsed : Change.Type.Expanded,
                                        id ) );
                    }
                } );
    }

    private LinkMenuItem collapsible( final Identifiable object,
                                      String expandedTitle, String collapsedTitle ) {

        final boolean expanded = getExpansions().contains( object.getId() );
        return new LinkMenuItem(
                "menuItem",
                new Model<String>( expanded ? expandedTitle : collapsedTitle ),
                new AjaxFallbackLink( "link" ) {
                    @Override
                    public void onClick( AjaxRequestTarget target ) {
                        update( target,
                                new Change( expanded ? Change.Type.Collapsed : Change.Type.Expanded,
                                        object ) );
                    }
                } );
    }

    private Segment getSegment() {
        return (Segment) getModel().getObject();
    }

}
