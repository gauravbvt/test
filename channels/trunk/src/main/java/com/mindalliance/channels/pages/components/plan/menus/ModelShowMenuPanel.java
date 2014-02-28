package com.mindalliance.channels.pages.components.plan.menus;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.dao.ModelManager;
import com.mindalliance.channels.core.model.CollaborationModel;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.pages.AbstractChannelsWebPage;
import com.mindalliance.channels.pages.Channels;
import com.mindalliance.channels.pages.components.menus.LinkMenuItem;
import com.mindalliance.channels.pages.components.menus.MenuPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
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
public class ModelShowMenuPanel extends MenuPanel {

    /**
     * Plan manager.
     */
    @SpringBean
    private ModelManager modelManager;

    /**
     * Survey service.
     */
    /*   @SpringBean
        private SurveyService surveyService;
    */
    public ModelShowMenuPanel( String s, IModel<? extends Segment> model, Set<Long> expansions ) {
        super( s, "Show", model, expansions );
    }


    @Override
    public String getHelpTopicId() {
        return "show-model";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<LinkMenuItem> getMenuItems() {
        synchronized ( getCommander() ) {
            CollaborationModel collaborationModel = getCollaborationModel();
            List<LinkMenuItem> menuItems = new ArrayList<LinkMenuItem>();

            menuItems.addAll(
                    Arrays.asList(
                            collapsible( Channels.SOCIAL_ID, "Hide presence", "Presence" ),
                            collapsible( collaborationModel, "Hide about model", "About model" ),
                            collapsible( getSegment(), "Hide about model segment", "About model segment" ),
                            collapsible( Channels.GALLERY_ID, "Gallery", "Gallery", "planner" ) ) );
            return menuItems;
        }
    }

    private LinkMenuItem newLink( String title, final CollaborationModel collaborationModel, final String action ) {
        return newLink(
                title,
                new AjaxLink( "link" ) {
                    @Override
                    public void onClick( AjaxRequestTarget target ) {
                        update( target, new Change( Change.Type.Expanded, collaborationModel, action ) );
                    }
                } );
    }

    private static LinkMenuItem newLink( String title, AjaxLink<?> link ) {
        return new LinkMenuItem(
                "menuItem",
                new Model<String>( title ),
                link );
    }

    private <T extends WebPage> BookmarkablePageLink<T> newTargetedLink(
            String target, Class<T> pageClass, PopupSettings popupSettings ) {
        return AbstractChannelsWebPage.newTargetedLink( "link", target, pageClass, popupSettings, getCollaborationModel() );
    }


    private Segment getSegment() {
        return (Segment) getModel().getObject();
    }

}
