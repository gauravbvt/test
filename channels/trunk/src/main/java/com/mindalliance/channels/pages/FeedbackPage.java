package com.mindalliance.channels.pages;

import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.pages.components.social.feedback.AllUserFeedbackPanel;
import com.mindalliance.channels.pages.components.support.UserFeedbackPanel;
import com.mindalliance.channels.social.model.Feedback;
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
public class FeedbackPage  extends AbstractChannelsWebPage {

    public FeedbackPage(  ) {
        this( new PageParameters() );
    }

    public FeedbackPage( PageParameters parameters ) {
        super( parameters );
        init();
    }

    private void init() {

        addHeading();
        addUserFeedbackPanel();
    }

    private void addHeading() {
        add( new Label( "planName", getPlan().getName() ) );
        add( new Label( "planVersion", "v" + getPlan().getVersion() ) );
        add( new UserFeedbackPanel(
                "feedback",
                getPlan(),
                "Send feedback",
                Feedback.FEEDBACK ) );
        add( new Label( "planDescription", getPlan().getName() ) );
    }

    private void addUserFeedbackPanel() {
        add( new AllUserFeedbackPanel(
                "allFeedback",
                new Model<Plan>( getPlan() ),
                false,
                true,
                true ) );
    }

}
