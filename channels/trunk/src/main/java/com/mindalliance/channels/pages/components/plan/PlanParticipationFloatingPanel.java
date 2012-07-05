package com.mindalliance.channels.pages.components.plan;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.pages.Channels;
import com.mindalliance.channels.pages.components.FloatingCommandablePanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * Plan Participation Floating Panel.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 7/4/12
 * Time: 6:55 PM
 */
public class PlanParticipationFloatingPanel extends FloatingCommandablePanel {

    private PlanParticipationPanel planParticipationPanel;

    public PlanParticipationFloatingPanel( String id, IModel<Plan> planModel ) {
        super( id, planModel );
        init();
    }

    private void init() {
        addHeading();
        addPlanParticipationPanel();
    }

    private void addHeading() {
        getContentContainer().add( new Label(
                "heading",
                "User participation as agents" ) );
    }

    private void addPlanParticipationPanel() {
        planParticipationPanel = new PlanParticipationPanel(
                "participation",
                new Model<Plan>(getPlan() ),
                null );
        getContentContainer().add( planParticipationPanel );
    }

    @Override
    protected void doClose( AjaxRequestTarget target ) {
        Change change = new Change( Change.Type.Collapsed, Channels.PLAN_PARTICIPATION);
        update( target, change );
    }

    @Override
    protected String getTitle() {
        return "User participation as agents";
    }

}

