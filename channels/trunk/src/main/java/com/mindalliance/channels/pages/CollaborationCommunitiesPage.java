package com.mindalliance.channels.pages;

import com.mindalliance.channels.db.data.messages.Feedback;
import com.mindalliance.channels.pages.components.community.AllCollaborationCommunitiesPanel;
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
public class CollaborationCommunitiesPage extends AbstractChannelsBasicPage {

    private AllCollaborationCommunitiesPanel allCollaborationCommunitiesPanel;

    public CollaborationCommunitiesPage() {
        this( new PageParameters() );
    }

    public CollaborationCommunitiesPage( PageParameters parameters ) {
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
        addAllCollaborationPlansPanel();
    }

    @Override
    protected void updateContent( AjaxRequestTarget target ) {
        allCollaborationCommunitiesPanel.updateContent( target );
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
        return "All Collaboration Communities";
    }

    @Override
    protected String getFeedbackType() {
        return Feedback.MODELS;
    }

    private void addAllCollaborationPlansPanel() {
        allCollaborationCommunitiesPanel = new AllCollaborationCommunitiesPanel( "allCommunities" );
        getContainer().add( allCollaborationCommunitiesPanel );
    }

}
