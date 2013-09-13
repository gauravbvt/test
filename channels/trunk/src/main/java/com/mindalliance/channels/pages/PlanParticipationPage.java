package com.mindalliance.channels.pages;

import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.db.data.messages.Feedback;
import com.mindalliance.channels.pages.components.manager.ParticipationManagerPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * Participation manager page.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/4/12
 * Time: 10:18 AM
 */
public class PlanParticipationPage extends AbstractChannelsBasicPage {

    private ParticipationManagerPanel participationManagerPanel;

    public PlanParticipationPage() {
        this( new PageParameters() );
    }

    public PlanParticipationPage( PageParameters parameters ) {
        super( parameters );
    }

    @Override
    protected String getHelpSectionId() {
        return "participation-page";
    }

    @Override
    protected String getHelpTopicId() {
        return "about-participation-page";
    }

    @Override
    protected void addContent() {
        addTitle();
        addParticipationManagerPanel();
    }

    private void addTitle() {
        getContainer().add( new Label("title", getPageName() ) );
    }

    private void addParticipationManagerPanel() {
        participationManagerPanel = new ParticipationManagerPanel(
                "participationManager",
                new Model<Plan>( getPlan() ) );
        getContainer().add( participationManagerPanel );
    }

    @Override
    protected String getContentsCssClass() {
        return "feedbacks-contents";
    }

    @Override
    public String getPageName() {
        return "Plan participation";
    }

    @Override
    protected String getFeedbackType() {
        return Feedback.PARTICIPATING;
    }

    @Override
    protected void updateContent( AjaxRequestTarget target ) {
        participationManagerPanel.updateContent( target );
    }

    @Override
    protected String getDefaultUserRoleId() {
        return "participant";
    }

}
