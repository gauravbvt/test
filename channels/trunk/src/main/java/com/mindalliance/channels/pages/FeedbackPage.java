package com.mindalliance.channels.pages;

import com.mindalliance.channels.core.model.CollaborationModel;
import com.mindalliance.channels.db.data.messages.Feedback;
import com.mindalliance.channels.pages.components.social.feedback.AllUserFeedbackPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * My feedback page.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 5/22/12
 * Time: 2:41 PM
 */
public class FeedbackPage  extends AbstractChannelsBasicPage {

    private AllUserFeedbackPanel allUserFeedbackPanel;

    public FeedbackPage(  ) {
        this( new PageParameters() );
    }

    public FeedbackPage( PageParameters parameters ) {
        super( parameters );
    }

    @Override
    protected String getHelpSectionId() {
        return "feedback-page";
    }

    @Override
    protected String getHelpTopicId() {
        return "about-feedback-page";
    }


    protected void addContent() {
        getContainer().add( new Label( "modelName", getCollaborationModel().getName() ) );
        addUserFeedbackPanel();
    }

    @Override
    protected void updateContent( AjaxRequestTarget target ) {
       allUserFeedbackPanel.updateContent( target );
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
        return "Feedback";
    }

    @Override
    protected String getFeedbackType() {
        return Feedback.FEEDBACK;
    }

    private void addUserFeedbackPanel() {
        allUserFeedbackPanel = new AllUserFeedbackPanel(
                "allFeedback",
                new Model<CollaborationModel>( getCollaborationModel() ),
                false,
                true,
                true );
        getContainer().add( allUserFeedbackPanel );
    }

}
