package com.mindalliance.channels.pages.components.support;

import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.JavascriptPackageResource;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

/**
 * Community support feedback widget.
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
    private static String JAVASCRIPT = "http://cdn.betaeasy.com/betaeasy.js";
    private static String PLAN_FEEDBACK_ICON = "/images/plan_feedback.png";
    private static String PLAN_FEEDBACK_HOVER = "/images/plan_feedback_hover.png";
    private static String CHANNELS_FEEDBACK_ICON = "/images/channels_feedback.png";
    private static String CHANNELS_FEEDBACK_HOVER = "/images/channels_feedback_hover.png";
    private static int FEEDBACK_ICON_WIDTH = 35;
    private static int PLAN_FEEDBACK_ICON_HEIGHT = 125;
    private static int CHANNELS_FEEDBACK_ICON_HEIGHT = 158;

    /**
     * Which community to join.
     */
    private IModel<String> communityModel;
    /**
     * Whether support is for a plan or else for Channels.
     */
    private boolean forPlanSupport;


    public FeedbackWidget( String id,
                           IModel<String> communityModel,
                           boolean isForPlanSupport ) {
        super( id );
        this.communityModel = communityModel;
        forPlanSupport = isForPlanSupport;
        add( JavascriptPackageResource.getHeaderContribution( JAVASCRIPT ) );
        init();
    }

    private void init() {
        final String script = "\nBetaEasy.init({"
                + "\nbetaId: '" + getCommunity() + "',"
                + "\nstyleType: 'new',"
                + "\nbuttonAlign: 'left',"
                + "\nlanguage: 'en',"
                + "\nbuttonBackgroundColor: '#f00',"
                + "\nbuttonMouseHoverBackgroundColor: '#06C',"
                + "\nbuttonImageActive: 'en/newbtn-8.png',"
                + "\nbuttonImageHover: 'none'"
                + "\n});";
        final String tweaks = "$(document).ready(function() {\n" +
                "  $('#BetaEasyInvokeButton a')" +
                ".css('top', '145px')" +
                ".css('width','" + FEEDBACK_ICON_WIDTH + "px" + "')" +
                ".css('height','" + getFeedbackIconHeight() + "px" + "')" +
                ".css('background','url(" + getFeedBackIconUrl( false ) + ") no-repeat')" +
                ".css('margin-left', '-15px')" +
                "\n" +
                "  $('#BetaEasyInvokeButton a').hover(" +
                "function() { $(this)" +
                ".css('top', '145px')" +
                ".css('width','" + FEEDBACK_ICON_WIDTH + "px" + "')" +
                ".css('height','" + getFeedbackIconHeight() + "px" + "')" +
                ".css('background','url(" + getFeedBackIconUrl( true ) + ") no-repeat')" +
                ".css('margin-left', '0')" +
                "}," +
                "function() { $(this)" +
                ".css('background','url(" + getFeedBackIconUrl( false ) + ") no-repeat')" +
                ".css('margin-left', '-15px')" +
                "})" +
                "});";
        this.add( new HeaderContributor( new IHeaderContributor() {
            public void renderHead( IHeaderResponse response ) {
                response.renderJavascript( script + '\n' + tweaks, null );
            }
        } ) );
    }

    private String getFeedBackIconUrl( boolean hover ) {
        if ( hover ) {
            return forPlanSupport
                    ? PLAN_FEEDBACK_HOVER
                    : CHANNELS_FEEDBACK_HOVER;
        } else {
            return forPlanSupport
                    ? PLAN_FEEDBACK_ICON
                    : CHANNELS_FEEDBACK_ICON;
        }
    }

    private int getFeedbackIconHeight() {
        return forPlanSupport
                ? PLAN_FEEDBACK_ICON_HEIGHT
                : CHANNELS_FEEDBACK_ICON_HEIGHT;
    }

    private String getCommunity() {
        return communityModel.getObject();
    }
}
