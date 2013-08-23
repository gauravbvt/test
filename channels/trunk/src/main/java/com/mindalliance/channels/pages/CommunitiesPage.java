package com.mindalliance.channels.pages;

import com.mindalliance.channels.db.data.messages.Feedback;
import com.mindalliance.channels.pages.components.community.AllCommunitiesPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * All communities page.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/11/13
 * Time: 11:22 AM
 */
public class CommunitiesPage extends AbstractChannelsBasicPage {

    private AllCommunitiesPanel allCommunitiesPanel;

    public CommunitiesPage() {
        this( new PageParameters() );
    }

    public CommunitiesPage( PageParameters parameters ) {
        super( parameters );
    }

    @Override
    protected String getHelpSectionId() {
        return "communities-page";
    }

    @Override
    protected String getHelpTopicId() {
        return "about-communities-page";
    }


    @Override
    protected void addContent() {
        addAllCommunitiesPanel();
    }

    @Override
    protected void updateContent( AjaxRequestTarget target ) {
        allCommunitiesPanel.updateContent( target );
    }

    @Override
    protected String getDefaultUserRoleId() {
        return "participant";
    }


    @Override
    protected String getContentsCssClass() {
        return "feedback-contents";
    }

    @Override
    public String getPageName() {
        return "Communities";
    }

    @Override
    protected String getFeedbackType() {
        return Feedback.CHANNELS;
    }

    private void addAllCommunitiesPanel() {
        allCommunitiesPanel = new AllCommunitiesPanel( "allCommunities" );
        getContainer().add( allCommunitiesPanel );
    }

}
