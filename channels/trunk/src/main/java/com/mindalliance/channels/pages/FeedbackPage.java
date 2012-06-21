package com.mindalliance.channels.pages;

import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.pages.components.social.feedback.AllUserFeedbackPanel;
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

    public FeedbackPage(  ) {
        this( new PageParameters() );
    }

    public FeedbackPage( PageParameters parameters ) {
        super( parameters );
    }

    protected void addContent() {
        addUserFeedbackPanel();
    }

    private void addUserFeedbackPanel() {
        getContainer().add( new AllUserFeedbackPanel(
                "allFeedback",
                new Model<Plan>( getPlan() ),
                false,
                true,
                true ) );
    }

}
