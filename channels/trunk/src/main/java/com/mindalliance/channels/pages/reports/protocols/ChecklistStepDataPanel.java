package com.mindalliance.channels.pages.reports.protocols;

import com.mindalliance.channels.api.procedures.checklist.ChecklistStepData;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.checklist.Checklist;
import com.mindalliance.channels.core.model.checklist.Step;
import com.mindalliance.channels.core.model.checklist.StepOrder;
import com.mindalliance.channels.db.data.messages.Feedback;
import com.mindalliance.channels.pages.components.support.UserFeedbackPanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;

import java.util.List;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/9/13
 * Time: 12:21 PM
 */
public abstract class ChecklistStepDataPanel extends AbstractDataPanel {

    private Part part;
    private ChecklistStepData stepData;
    private int index;
    private WebMarkupContainer stepContainer;
    private boolean showingMore = false;
    private AjaxLink<String> moreLessButton;
    private WebMarkupContainer moreContainer;
    private Label actLabel;

    public ChecklistStepDataPanel( String id,
                                   Part part,
                                   ChecklistStepData stepData,
                                   int index,
                                   ProtocolsFinder finder,
                                   boolean allExpanded ) {
        super( id, finder );
        this.part = part;
        this.stepData = stepData;
        this.index = index;
        showingMore = allExpanded;
        init();
    }

    protected abstract String getStepAct();

    protected abstract String getInstructions();

    protected Component makeStepDetailsPanel( String id ) {
        return new Label( id, "" ); // DEFAULT
    }

    protected boolean hasMore() { // DEFAULT
        return getChecklist().hasLogic( getStep() );
    }

    private void init() {
        addStepContainer();
        addMoreContainer();
    }

    private void addStepContainer() {
        stepContainer = new WebMarkupContainer( "stepContainer" );
        stepContainer.setOutputMarkupId( true );
        addOrReplace( stepContainer );
        updateStepCss();
        addStepAct();
        addInstructions();
        addMoreLessButton();
        addFeedbackPanel();
    }

    private void addStepAct() {
        actLabel = new Label( "act", getStepAct() );
        actLabel.setOutputMarkupId( true );
        actLabel.add( new AttributeModifier( "class", getActCss() ) );
        addTipTitle( actLabel, getStep().isRequired() ? "Expected" : "Optional" );
        stepContainer.addOrReplace( actLabel );
    }

    protected String getActCss() {
        return getStep().isRequired() ? "required step-act" : "optional step-act";
    }

    private void updateStepCss() {
        String cssClasses = showingMore ? "checklist-step-title active" : "checklist-step-title";
        stepContainer.add( new AttributeModifier( "class", cssClasses ) );
    }


    private void addInstructions() {
        String instructions = getInstructions();
        Label instructionsLabel = new Label( "instructions", instructions );
        instructionsLabel.setVisible( !instructions.isEmpty() );
        stepContainer.add( instructionsLabel );
    }


    private void addMoreLessButton() {
        moreLessButton = new AjaxLink<String>( "moreLessButton" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                showingMore = !showingMore;
                addStepContainer();
                addMoreContainer();
                target.add( stepContainer );
                target.add( moreContainer );
            }
        };
        moreLessButton.setVisible( hasMore() );
        moreLessButton.setOutputMarkupId( true );
        moreLessButton.add( new Label( "moreLess", isExpanded() ? "- Less" : "+ More" ) );
        moreLessButton.add( new AttributeModifier( "class", "more" ) );
        stepContainer.addOrReplace( moreLessButton );
    }

    private boolean isExpanded() {
        return showingMore;
    }

    private void addFeedbackPanel() {
        UserFeedbackPanel feedbackPanel = new UserFeedbackPanel(
                "feedback",
                getChecklist().getPart(),
                "Feedback",
                Feedback.CHECKLISTS,
                "the step " + getStep().getLabel()
        );
        stepContainer.addOrReplace( feedbackPanel );
    }


    private void addMoreContainer() {
        moreContainer = new WebMarkupContainer( "moreContainer" );
        moreContainer.setOutputMarkupId( true );
        makeVisible( moreContainer, showingMore && hasMore() );
        addOrReplace( moreContainer );
        moreContainer.addOrReplace( new StepLogicPanel( "stepLogic", part, getStepData(), getFinder() ) );
        moreContainer.addOrReplace( makeStepDetailsPanel( "stepDetails" ) );
    }


    ////////////


    protected List<StepOrder> getStepOrders() {
        return getChecklist().listStepOrdersFor( getStep() );
    }

    protected Checklist getChecklist() {
        return part.getEffectiveChecklist();
    }

    protected Step getStep() {
        return stepData.getStep();
    }

    protected ChecklistStepData getStepData() {
        return stepData;
    }


}
