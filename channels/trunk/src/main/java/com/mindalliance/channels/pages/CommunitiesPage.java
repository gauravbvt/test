package com.mindalliance.channels.pages;

import com.mindalliance.channels.pages.components.community.AllCommunitiesPanel;
import com.mindalliance.channels.social.model.Feedback;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * All plan communities page.
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
    protected void addContent() {
        addAllCommunitiesPanel();
    }

    @Override
    protected void updateContent( AjaxRequestTarget target ) {
        allCommunitiesPanel.updateContent( target );
    }


    @Override
    protected String getContentsCssClass() {
        return "feedback-contents";
    }

    @Override
    protected String getPageName() {
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