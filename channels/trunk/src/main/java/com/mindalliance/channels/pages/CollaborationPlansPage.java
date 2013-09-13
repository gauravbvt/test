package com.mindalliance.channels.pages;

import com.mindalliance.channels.db.data.messages.Feedback;
import com.mindalliance.channels.pages.components.community.AllCollaborationPlansPanel;
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
public class CollaborationPlansPage extends AbstractChannelsBasicPage {

    private AllCollaborationPlansPanel allCollaborationPlansPanel;

    public CollaborationPlansPage() {
        this( new PageParameters() );
    }

    public CollaborationPlansPage( PageParameters parameters ) {
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
        allCollaborationPlansPanel.updateContent( target );
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
        return "All Collaboration plans";
    }

    @Override
    protected String getFeedbackType() {
        return Feedback.CHANNELS;
    }

    private void addAllCollaborationPlansPanel() {
        allCollaborationPlansPanel = new AllCollaborationPlansPanel( "allCollaborationPlans" );
        getContainer().add( allCollaborationPlansPanel );
    }

}
