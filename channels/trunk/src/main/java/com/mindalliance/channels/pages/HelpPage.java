package com.mindalliance.channels.pages;

import com.mindalliance.channels.dao.PlanManager;
import com.mindalliance.channels.pages.components.support.FeedbackWidget;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Help page.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 27, 2010
 * Time: 1:21:18 PM
 */
public class HelpPage extends WebPage {

    /**
     * Plan manager.
     */
    @SpringBean
    private PlanManager planManager;


    public HelpPage() {
        setStatelessHint( true );
        addFeedbackWidget();
    }

    private void addFeedbackWidget() {
        FeedbackWidget feedbackWidget = new FeedbackWidget(
                "feedback-widget",
                new Model<String>(
                        getApp().getSupportCommunityUri( ) ),
                false );
        makeVisible( feedbackWidget, false );
        add( feedbackWidget );
    }

    /**
     * Set a component's visibility.
     *
     * @param component a component
     * @param visible   a boolean
     */
    private static void makeVisible( Component component, boolean visible ) {
        component.add( new AttributeModifier( "style", true, new Model<String>(
                visible ? "" : "display:none" ) ) );
    }

    private Channels getApp() {
        return (Channels) getApplication();
    }


}
