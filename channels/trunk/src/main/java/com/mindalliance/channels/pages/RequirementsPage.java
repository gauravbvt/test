package com.mindalliance.channels.pages;

import com.mindalliance.channels.db.data.messages.Feedback;
import com.mindalliance.channels.pages.components.community.requirements.RequirementsManagerPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * Requirements page.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 1/9/13
 * Time: 9:52 AM
 */
public class RequirementsPage extends AbstractChannelsBasicPage {

    private RequirementsManagerPanel requirementsManagerPanel;

    public RequirementsPage() {
    }

    public RequirementsPage( PageParameters parameters ) {
        super( parameters );

    }

    @Override
    protected String getHelpSectionId() {
        return "requirements-page";
    }

    @Override
    protected String getHelpTopicId() {
        return requirementsManagerPanel.getTabTopicId();
    }

    @Override
    protected void addContent() {
        addTitle();
        addRequirementsPanel();
    }

    @Override
    public boolean isCommunityContext() {
        return true;
    }

    @Override
    public boolean isPlanContext() {
        return false;
    }

    private void addRequirementsPanel() {
        requirementsManagerPanel = new RequirementsManagerPanel(
               "requirementsManager",
                getReadOnlyExpansions()
       );
        getContainer().add( requirementsManagerPanel );
    }

    private void addTitle() {
        getContainer().add( new Label("title", getPageName() ) );
    }

    @Override
    protected String getContentsCssClass() {
        return "feedbacks-contents";
    }

    @Override
    public String getPageName() {
        return "Collaboration requirements";
    }

    @Override
    protected String getFeedbackType() {
        return Feedback.REQUIREMENTS;
    }

    @Override
    protected void updateContent( AjaxRequestTarget target ) {
        requirementsManagerPanel.updateContent( target );
    }

    @Override
    protected String getDefaultUserRoleId() {
        return "participant";
    }

}
