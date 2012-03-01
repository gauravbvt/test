package com.mindalliance.channels.pages.components.social;

import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.social.model.Feedback;
import org.apache.wicket.model.IModel;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/1/12
 * Time: 3:31 PM
 */
public class FeedbackStatementPanel extends UserStatementPanel {

    public FeedbackStatementPanel(
            String id,
            IModel<Feedback> feedbackModel,
            int index,
            Updatable updatable ) {
        super( id, feedbackModel, index, updatable );
    }

    protected String getCssClasses() {
        String cssClasses = getFeedback().isUrgent()
                ? "urgent "
                : "not-urgent ";
        cssClasses += getFeedback().getTypeLabel();
        return cssClasses + " " + super.getCssClasses();
    }

    private Feedback getFeedback() {
        return (Feedback)getUserStatement();
    }

}
