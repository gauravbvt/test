package com.mindalliance.channels.pages;

import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.pages.components.manager.ParticipationManagerPanel;
import com.mindalliance.channels.social.model.Feedback;
import org.apache.wicket.ajax.AjaxRequestTarget;
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
public class ParticipationManagerPage extends AbstractChannelsBasicPage {

    private ParticipationManagerPanel participationManagerPanel;

    public ParticipationManagerPage() {
        this( new PageParameters(  ) );
    }

    public ParticipationManagerPage( PageParameters parameters ) {
        super( parameters );
    }

    @Override
    protected void addContent() {
        addParticipationManagerPanel();
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
    protected String getPageName() {
        return "Participation manager";
    }

    @Override
    protected String getFeedbackType() {
        return Feedback.PARTICIPATING;
    }

    @Override
    protected void updateContent( AjaxRequestTarget target ) {
        participationManagerPanel.updateContent( target );
    }

}
