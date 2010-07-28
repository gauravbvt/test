package com.mindalliance.channels.pages.components.support;

import org.apache.wicket.ResourceReference;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.JavascriptPackageResource;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.html.resources.JavascriptResourceReference;
import org.apache.wicket.model.IModel;

/**
 * GetSatisfaction feedback widget.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 28, 2010
 * Time: 11:03:01 AM
 */
public class FeedbackWidget extends Panel {

    /**
     * JavaScript.
     */
    private static ResourceReference JAVASCRIPT = new JavascriptResourceReference(
            FeedbackWidget.class, "res/FeedbackWidgetPanel.js" );
    private IModel<String> communityModel;


    public FeedbackWidget( String id, IModel<String> communityModel ) {
        super( id );
        this.communityModel = communityModel;
        add( JavascriptPackageResource.getHeaderContribution( JAVASCRIPT ) );
        init();
    }

    private void init() {
       final String script = "feedback_widget_options.company = \"" + getCommunity() + "\";"
               + "\n var feedback_widget = new GSFN.feedback_widget(feedback_widget_options);";
       this.add( new HeaderContributor(new IHeaderContributor() {
           public void renderHead( IHeaderResponse response ) {
                response.renderOnDomReadyJavascript( script );
           }
       }));
    }

    private String getCommunity() {
        return communityModel.getObject();
    }
}
