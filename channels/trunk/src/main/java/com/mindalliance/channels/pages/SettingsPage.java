package com.mindalliance.channels.pages;

import com.mindalliance.channels.db.data.messages.Feedback;
import com.mindalliance.channels.pages.components.settings.SettingsPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.request.http.flow.AbortWithHttpErrorCodeException;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import javax.servlet.http.HttpServletResponse;

/**
 * Channels Settings Page.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 6/22/13
 * Time: 11:40 AM
 */
public class SettingsPage extends AbstractChannelsBasicPage {

    private SettingsPanel settingsPanel;

    public SettingsPage() {
        if ( !getUser().isAdmin() )
            throw new AbortWithHttpErrorCodeException( HttpServletResponse.SC_FORBIDDEN, "Unauthorized access" );
    }

    public SettingsPage( PageParameters parameters ) {
        super( parameters );
        if ( !getUser().isAdmin() )
            throw new AbortWithHttpErrorCodeException( HttpServletResponse.SC_FORBIDDEN, "Unauthorized access" );
    }


    @Override
    protected String getHelpSectionId() {
        return "settings-page";
    }

    @Override
    protected String getHelpTopicId() {
        return "about-settings-page";
    }

    @Override
    protected void addContent() {
        addTitle();
        addSettingsPanel();
    }

    @Override
    public boolean isCommunityContext() {
        return false;
    }

    @Override
    public boolean isPlanContext() {
        return false;
    }

    private void addTitle() {
        getContainer().add( new Label("title", getPageName() ) );
    }

    private void addSettingsPanel() {
        settingsPanel = new SettingsPanel(
                "settings"
        );
        getContainer().add( settingsPanel );
    }


    @Override
    protected String getContentsCssClass() {
        return "feedbacks-contents";
    }

    @Override
    public String getPageName() {
        return "Channels settings";
    }

    @Override
    protected String getFeedbackType() {
        return Feedback.CHANNELS;
    }

    @Override
    protected void updateContent( AjaxRequestTarget target ) {
        settingsPanel.updateContent( target );
    }

    @Override
    protected String getDefaultUserRoleId() {
        return "admin";
    }




}
