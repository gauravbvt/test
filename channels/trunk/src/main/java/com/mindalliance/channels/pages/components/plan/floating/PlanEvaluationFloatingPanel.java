package com.mindalliance.channels.pages.components.plan.floating;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.pages.Channels;
import com.mindalliance.channels.pages.components.AbstractFloatingCommandablePanel;
import com.mindalliance.channels.pages.components.plan.PlanEvaluationPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 7/4/12
 * Time: 5:40 PM
 */
public class PlanEvaluationFloatingPanel extends AbstractFloatingCommandablePanel {

    private PlanEvaluationPanel planEvaluationPanel;

    public PlanEvaluationFloatingPanel( String id, IModel<Plan> planModel ) {
        super( id, planModel );
        init();
    }

    @Override
    public String getHelpSectionId() {
        return "improving";
    }

    @Override
    public String getHelpTopicId() {
        return "plan-evaluation";
    }

    private void init() {
        addHeading();
        addPlanEvaluationPanel();
    }

    private void addHeading() {
        getContentContainer().add( new Label(
                "heading",
                "Collaboration model evaluation" ) );
    }

    private void addPlanEvaluationPanel() {
        planEvaluationPanel = new PlanEvaluationPanel(
                "evaluation",
                new Model<Plan>(getPlan() ),
                null );
        getContentContainer().add( planEvaluationPanel );
    }

    @Override
    protected void doClose( AjaxRequestTarget target ) {
        Change change = new Change( Change.Type.Collapsed, Channels.PLAN_EVALUATION);
        update( target, change );
    }

    @Override
    protected String getTitle() {
        return "Collaboration model evaluation";
    }

}
