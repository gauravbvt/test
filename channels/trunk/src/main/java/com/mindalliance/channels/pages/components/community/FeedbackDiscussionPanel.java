package com.mindalliance.channels.pages.components.community;

import com.mindalliance.channels.core.community.feedback.Feedback;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import org.apache.wicket.model.Model;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/22/12
 * Time: 5:54 PM
 */
public class FeedbackDiscussionPanel extends AbstractUpdatablePanel {

    public FeedbackDiscussionPanel( String id, Model<Feedback> feedbackModel ) {
        super( id, feedbackModel );
        init();
    }

    private void init() {
        // todo
    }
}
