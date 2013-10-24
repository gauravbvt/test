package com.mindalliance.channels.pages.reports.protocols;

import com.mindalliance.channels.api.procedures.TaskData;
import com.mindalliance.channels.api.procedures.checklist.ChecklistStepData;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.checklist.Checklist;
import com.mindalliance.channels.core.model.checklist.Condition;
import com.mindalliance.channels.core.model.checklist.Outcome;
import com.mindalliance.channels.core.model.checklist.Step;
import com.mindalliance.channels.core.model.checklist.StepGuard;
import com.mindalliance.channels.core.model.checklist.StepOrder;
import com.mindalliance.channels.core.model.checklist.StepOutcome;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import java.util.List;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 10/23/13
 * Time: 11:53 AM
 */
public class StepLogicPanel extends AbstractDataPanel {

    private Part part;
    private ChecklistStepData stepData;

    private static final int MAX_SIZE = 65;
    private static final boolean POSITIVE = true;

    WebMarkupContainer logicContainer;
    WebMarkupContainer outcomesContainer;

    public StepLogicPanel( String id, Part part, ChecklistStepData stepData, ProtocolsFinder finder ) {
        super( id, finder );
        this.part = part;
        this.stepData = stepData;
        init();
    }

    private void init() {
        addConstraints();
        addOutcomes();
    }

    private void addConstraints() {
        logicContainer = new WebMarkupContainer( "logicContainer" );
        logicContainer.setVisible( hasLogic( getStep() ) );
        add( logicContainer );
        addIfGuards();
        addUnlessGuards();
        addPrerequisiteSteps();
    }

    private boolean hasLogic( Step step ) {
        return getChecklist().hasGuards( step, POSITIVE )
                || getChecklist().hasGuards( step, !POSITIVE )
                || getChecklist().hasPrerequisites( step )
                || getChecklist().hasOutcomes( step );
    }

    private void addIfGuards() {
        WebMarkupContainer ifsContainer = new WebMarkupContainer( "ifsContainer" );
        ifsContainer.setVisible( getChecklist().hasGuards( getStep(), POSITIVE ) );
        logicContainer.add( ifsContainer );
        List<StepGuard> ifGuards = getIfGuards();
        ListView<StepGuard> ifListView = new ListView<StepGuard>(
                "ifs",
                ifGuards
        ) {
            @Override
            protected void populateItem( ListItem<StepGuard> item ) {
                final StepGuard stepGuard = item.getModelObject();
                Condition condition = getChecklist().deRefCondition( stepGuard.getConditionRef() );
                String conditionString = condition == null ? "UNDEFINED" : condition.getLabel();
                Label label = new Label( "if", StringUtils.abbreviate( conditionString, MAX_SIZE ) );
                item.add( label );
                if ( conditionString.length() > MAX_SIZE ) {
                    addTipTitle( label, conditionString );
                }

            }
        };
        ifListView.setOutputMarkupId( true );
        ifListView.setVisible( !ifGuards.isEmpty() );
        ifsContainer.add( ifListView );
    }

    private List<StepGuard> getIfGuards() {
        return getChecklist().listEffectiveStepGuards( getStep(), true );
    }

    private void addUnlessGuards() {
        List<StepGuard> unlessGuards = getUnlessGuards();
        WebMarkupContainer unlessesContainer = new WebMarkupContainer( "unlessesContainer" );
        unlessesContainer.setVisible( getChecklist().hasGuards( getStep(), !POSITIVE ) );
        logicContainer.add( unlessesContainer );
        ListView<StepGuard> unlessListView = new ListView<StepGuard>(
                "unlesses",
                unlessGuards
        ) {
            @Override
            protected void populateItem( ListItem<StepGuard> item ) {
                final StepGuard stepGuard = item.getModelObject();
                Condition condition = getChecklist().deRefCondition( stepGuard.getConditionRef() );
                String conditionString = condition == null ? "UNDEFINED" : condition.getLabel();
                Label label = new Label( "unless", StringUtils.abbreviate( conditionString, MAX_SIZE ) );
                item.add( label );
                if ( conditionString.length() > MAX_SIZE ) {
                    addTipTitle( label, conditionString );
                }
            }
        };
        unlessListView.setOutputMarkupId( true );
        unlessListView.setVisible( !unlessGuards.isEmpty() );
        unlessesContainer.add( unlessListView );
    }

    @SuppressWarnings("unchecked")
    private List<StepGuard> getUnlessGuards() {
        return getChecklist().listEffectiveStepGuards( getStep(), false );
    }

    private void addPrerequisiteSteps() {
        List<StepOrder> stepOrders = getStepOrders();
        WebMarkupContainer aftersContainer = new WebMarkupContainer( "aftersContainer" );
        aftersContainer.setVisible( getChecklist().hasPrerequisites( getStep() ) );
        logicContainer.add( aftersContainer );
        ListView<StepOrder> prerequisiteListView = new ListView<StepOrder>(
                "afters",
                stepOrders
        ) {
            @Override
            protected void populateItem( ListItem<StepOrder> item ) {
                final StepOrder stepOrder = item.getModelObject();
                Step prerequisite = getChecklist().derefStep( stepOrder.getPrerequisiteStepRef() );
                String label = prerequisite.getPrerequisiteLabel();
                Label stepLabel = new Label( "after", StringUtils.abbreviate( label, MAX_SIZE ) );
                if ( label.length() > MAX_SIZE ) {
                    addTipTitle( stepLabel, label );
                }
                item.add( stepLabel );
            }
        };
        aftersContainer.add( prerequisiteListView );
    }

    private void addOutcomes() {
        outcomesContainer = new WebMarkupContainer( "outcomesContainer" );
        outcomesContainer.setVisible( getChecklist().hasOutcomes( getStep() ) );
        logicContainer.add( outcomesContainer );
        addStepOutcomes();
    }

    private void addStepOutcomes() {
        List<StepOutcome> stepOutcomes = getChecklist().listEffectiveStepOutcomesFor( getStep() );
        ListView<StepOutcome> outcomeListView = new ListView<StepOutcome>(
                "outcomes",
                stepOutcomes
        ) {
            @Override
            protected void populateItem( ListItem<StepOutcome> item ) {
                final StepOutcome stepOutcome = item.getModelObject();
                Outcome outcome = getChecklist().deRefOutcome( stepOutcome.getOutcomeRef() );
                String outcomeString = outcome == null ? "UNDEFINED" : outcome.getLabel();
                Label label = new Label( "outcome", StringUtils.abbreviate( outcomeString, MAX_SIZE ) );
                item.add( label );
                if ( outcomeString.length() > MAX_SIZE ) {
                    addTipTitle( label, outcomeString );
                }
            }
        };
        outcomeListView.setOutputMarkupId( true );
        outcomeListView.setVisible( !stepOutcomes.isEmpty() );
        outcomesContainer.add( outcomeListView );
    }

    protected List<StepOrder> getStepOrders() {
        return getChecklist().listStepOrdersFor( getStep() );
    }

    protected Checklist getChecklist() {
        return getPart().getEffectiveChecklist();
    }

    private Part getPart() {
        return part;
    }

    protected Step getStep() {
        return stepData.getStep();
    }

    protected ChecklistStepData getStepData() {
        return stepData;
    }

    private void addTaskLink() { // todo - add task link to subtask steps
        WebMarkupContainer link = new WebMarkupContainer( "link" );
        link.add( new AttributeModifier( "href", "#" + getSubOrFollowUpTask().getAnchor() ) );
        add( link );
        link.add( new Label( "taskName",
                "I do task \"" + getSubOrFollowUpTask().getLabel() + "\"" ) );

    }

    private TaskData getSubOrFollowUpTask() {
        return null;  //Todo
    }

}
