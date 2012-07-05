package com.mindalliance.channels.pages.components.plan.menus;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.dao.PlanManager;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.pages.AbstractChannelsWebPage;
import com.mindalliance.channels.pages.Channels;
import com.mindalliance.channels.pages.components.menus.LinkMenuItem;
import com.mindalliance.channels.pages.components.menus.MenuPanel;
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
    public List<LinkMenuItem> getMenuItems() {
        synchronized ( getCommander() ) {
            Plan plan = getPlan();
            List<LinkMenuItem> menuItems = new ArrayList<LinkMenuItem>();

            menuItems.addAll(
                    Arrays.asList(
                            collapsible( Channels.SOCIAL_ID, "Hide planners", "Planners" ),
                            collapsible( Channels.GUIDE_ID, "Hide guide", "Guide" ),
                            collapsible( plan, "Hide about plan", "About plan" ),
                            collapsible(
                                    getSegment(), "Hide about plan segment", "About plan segment" ) ) );

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


    private Segment getSegment() {
        return (Segment) getModel().getObject();
    }

}
