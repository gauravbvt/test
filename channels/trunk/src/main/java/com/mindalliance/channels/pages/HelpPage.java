package com.mindalliance.channels.pages;

import com.mindalliance.channels.db.data.messages.Feedback;
import com.mindalliance.channels.pages.components.support.UserFeedbackPanel;
import org.apache.wicket.markup.html.WebPage;


/**
 * Help page.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 27, 2010
 * Time: 1:21:18 PM
 */
public class HelpPage extends WebPage {

    public HelpPage() {
        setStatelessHint( true );
        init();
    }

    private void init() {
        add( new UserFeedbackPanel( "feedback", Feedback.MODELS ) );
    }

}
