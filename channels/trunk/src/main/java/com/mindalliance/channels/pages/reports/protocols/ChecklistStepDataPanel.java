package com.mindalliance.channels.pages.reports.protocols;

import com.mindalliance.channels.api.procedures.checklist.ChecklistStepData;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.checklist.Checklist;
import com.mindalliance.channels.core.model.checklist.Step;
import com.mindalliance.channels.core.model.checklist.StepOrder;
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

    public ChecklistStepDataPanel( String id, Part part, ChecklistStepData stepData, int index, ProtocolsFinder finder ) {
        super( id, finder );
        this.part = part;
        this.stepData = stepData;
        this.index = index;
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
        stepContainer = new WebMarkupContainer( "stepContainer" );
        String cssClasses = index % 2 == 0 ? "checklist-step-title" : "checklist-step-title";
        stepContainer.add( new AttributeModifier( "class", cssClasses ) );
        add( stepContainer );
        addStepAct();
        addInstructions();
        addMoreLessButton();
        addMoreContainer();
    }

    private void addStepAct() {
        Label actLabel = new Label( "act", getStepAct());
        actLabel.add( new AttributeModifier( "class", getActCss() ) );
        stepContainer.add( actLabel );
    }

    protected String getActCss() {
        return getStep().isRequired() ? "required step-act" : "optional step-act";
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
                addMoreLessButton();
                target.add( moreLessButton );
                addMoreContainer();
                target.add( moreContainer );
            }
        };
        moreLessButton.setVisible( hasMore() );
        moreLessButton.setOutputMarkupId( true );
        moreLessButton.add( new Label( "moreLess", showingMore ? "- Less" : "+ More" ) );
        moreLessButton.add( new AttributeModifier( "class", "more" ) );
        stepContainer.addOrReplace( moreLessButton );
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
