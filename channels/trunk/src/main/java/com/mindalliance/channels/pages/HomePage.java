package com.mindalliance.channels.pages;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.dao.PlanManager;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.db.data.messages.Feedback;
import com.mindalliance.channels.pages.components.social.SocialPanel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

/**
 * Channels Home Page.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/11/13
 * Time: 9:43 AM
 */
public class HomePage extends AbstractChannelsBasicPage {


    @SpringBean
    private PlanManager planManager;

    private SocialPanel socialPanel;
    private WebMarkupContainer gotoIconsContainer;


    public HomePage() {
        this( new PageParameters() );
    }

    public HomePage( PageParameters parameters ) {
        super( parameters );
    }


    protected boolean hasBreadCrumbs() {
        return false; // default
    }

    @Override
    protected String getContentsCssClass() {
        return "home-contents";
    }

    @Override
    public String getPageName() {
        return "Home";
    }

    @Override
    protected String getFeedbackType() {
        return Feedback.CHANNELS;
    }

    @Override
    protected void addContent() {
        addGotoLinks( getUser() );
        addSocial();
    }

    @Override
    protected void updateContent( AjaxRequestTarget target ) {
        addGotoLinks( getUser() );
        target.add( gotoIconsContainer );
    }

    @Override
    protected String getDefaultUserRoleId() {
        return "user";
    }


    private void addGotoLinks( ChannelsUser user ) {
        gotoIconsContainer = new WebMarkupContainer( "goto-icons" );
        gotoIconsContainer.setOutputMarkupId( true );
        getContainer().addOrReplace( gotoIconsContainer );

        // Communities link
        BookmarkablePageLink<? extends WebPage> gotoCommunitiesLink =
                newTargetedLink( "gotoCommunities", CommunitiesPage.class );
        addTipTitle( gotoCommunitiesLink, new Model<String>( getGotoCommunitiesDescription() ) );
        // Models link
        BookmarkablePageLink gotoModelsLink = newTargetedLink( "gotoModels", PlansPage.class );
        addTipTitle( gotoModelsLink,
                new Model<String>( getGotoModelsDescription() )
        );

        // Settings
        BookmarkablePageLink gotoAdminLink = newTargetedLink( "gotoAdmin", SettingsPage.class );
        addTipTitle(
                gotoAdminLink,
                "Configure Channels, add users, change access privileges, and create, configure, release, or delete collaboration models" );

        // gotos
        gotoIconsContainer.add(
                // Goto admin
                new WebMarkupContainer( "admin" )
                        .add( gotoAdminLink )
                        .setVisible( user.isAdmin() )
                        .setOutputMarkupId( true ),

                // Goto model
                new WebMarkupContainer( "models" )
                        .add( gotoModelsLink )
                        .setVisible( hasAccessToPlans() )
                        .setOutputMarkupId( true ),

                // Goto protocols
                new WebMarkupContainer( "communities" )
                        .add( gotoCommunitiesLink )
                        .setOutputMarkupId( true ) );

    }

    private void addSocial() {
        String[] tabsShown = {SocialPanel.USER};
        socialPanel = new SocialPanel( "social", false, tabsShown, false );
        getContainer().add( socialPanel );
    }

    private String getGotoModelsDescription() {
        return "The collaboration models you are developing or maintaining"; // todo show metrics
    }

    private String getGotoCommunitiesDescription() {
        return "The collaboration communities you participate or could participate in"; // todo show metrics
    }

    private boolean hasAccessToPlans() {
        return CollectionUtils.exists(
                planManager.getPlans(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return getUser().hasAccessTo( ( (Plan) object ).getUri() );
                    }
                }
        );
    }


    @Override
    public void update( AjaxRequestTarget target, Object object, String action ) {
        // do nothing
    }

    @Override
    public void refresh( AjaxRequestTarget target, Change change, List<Updatable> updated, String aspect ) {
        // do nothing
    }

    @Override
    public void refresh( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        // do nothing
    }

    @Override
    public void refresh( AjaxRequestTarget target, Change change ) {
        //To change body of implemented methods use File | Settings | File Templates.
    }


}
