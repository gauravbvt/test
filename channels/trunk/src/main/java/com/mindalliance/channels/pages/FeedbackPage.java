package com.mindalliance.channels.pages;

import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.pages.components.social.feedback.AllUserFeedbackPanel;
import com.mindalliance.channels.social.model.Feedback;
import org.apache.wicket.ajax.AjaxRequestTarget;
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

    protected void addContent() {
        addUserFeedbackPanel();
    }

    @Override
    protected void updateContent( AjaxRequestTarget target ) {
        target.add( allUserFeedbackPanel );
    }

    @Override
    protected String getContentsCssClass() {
        return "feedback-contents";
    }

    @Override
    protected String getPageName() {
        return "Feedback";
    }

    @Override
    protected String getFeedbackType() {
        return Feedback.FEEDBACK;
    }

    private void addUserFeedbackPanel() {
        allUserFeedbackPanel = new AllUserFeedbackPanel(
                "allFeedback",
                new Model<Plan>( getPlan() ),
                false,
                true,
                true );
        getContainer().add( allUserFeedbackPanel );
    }

}
