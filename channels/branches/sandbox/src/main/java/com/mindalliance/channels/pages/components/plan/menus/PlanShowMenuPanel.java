package com.mindalliance.channels.pages.components.plan.menus;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.dao.PlanManager;
import com.mindalliance.channels.dao.User;
import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.pages.AdminPage;
import com.mindalliance.channels.pages.Channels;
import com.mindalliance.channels.pages.HelpPage;
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
    @SpringBean
    private SurveyService surveyService;

    public PlanShowMenuPanel( String s, IModel<? extends Segment> model, Set<Long> expansions ) {
        super( s, "Show", model, expansions );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Component> getMenuItems() {
        synchronized ( getCommander() ) {
            Plan plan = User.plan();
            List<Component> menuItems = new ArrayList<Component>();

            menuItems.addAll(
                    Arrays.asList(
                            collapsible( Channels.SOCIAL_ID, "Hide planners", "Planners" ),
                            collapsible( plan, "Hide about plan", "About plan" ),
                            collapsible(
                                    getSegment(), "Hide about plan segment", "About plan segment" ) ) );

            if ( plan.isDevelopment() )
                menuItems.add( collapsible( Survey.UNKNOWN, "Hide surveys", "Surveys" ) );

            menuItems.add(
                    newLink(
                            "Procedures report",
                            newTargetedLink( "_blank", SOPsReportPage.class, null ) ) );

             if ( User.current().isAdmin() )
                menuItems.add(
                        newLink(
                                "Admin page",
                                new BookmarkablePageLink<AdminPage>( "link", AdminPage.class ) ) );
            menuItems.addAll(
                    Arrays.asList(
                            newLink( "All segments", plan, PlanEditPanel.MAP ),
                            newLink( "All issues", plan, PlanEditPanel.ISSUES ),
                            newLink( "Index", plan, PlanEditPanel.INDEX ),
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

    private static <T extends WebPage> BookmarkablePageLink<T> newTargetedLink(
            String target, Class<T> pageClass, PopupSettings popupSettings ) {

        BookmarkablePageLink<T> link = new BookmarkablePageLink<T>( "link", pageClass );
        link.add( new AttributeModifier( "target", true, new Model<String>( target ) ) );
        if ( popupSettings != null )
            link.setPopupSettings( popupSettings );

        return link;
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
