package com.mindalliance.channels.pages.reports.protocols;

import com.mindalliance.channels.api.procedures.TaskData;
import com.mindalliance.channels.api.procedures.checklist.ChecklistStepData;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.checklist.ActionStep;
import com.mindalliance.channels.core.model.checklist.Checklist;
import com.mindalliance.channels.core.model.checklist.Condition;
import com.mindalliance.channels.core.model.checklist.Step;
import com.mindalliance.channels.core.model.checklist.StepGuard;
import com.mindalliance.channels.core.model.checklist.StepOrder;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import java.util.List;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/9/13
 * Time: 12:21 PM
 */
public class ChecklistStepDataPanel extends AbstractDataPanel {

    private static final int MAX_SIZE = 65;
    private static final boolean POSITIVE = true;

    private Part part;
    private ChecklistStepData stepData;
    private int index;
    private WebMarkupContainer stepContainer;
    private WebMarkupContainer constraintsContainer;

    public ChecklistStepDataPanel( String id, Part part, ChecklistStepData stepData, int index, ProtocolsFinder finder ) {
        super( id, finder );
        this.part = part;
        this.stepData = stepData;
        this.index = index;
        init();
    }

    private void init() {
        stepContainer = new WebMarkupContainer( "stepContainer" );
        String cssClasses = index % 2 == 0 ? "data-table step even-step" : "data-table step odd-step";
        stepContainer.add( new AttributeModifier( "class", cssClasses ) );
        add( stepContainer );
        addStepAct();
        addConstraints();
    }


    public boolean isRequired() {
        return getStep().isActionStep() && ( (ActionStep) getStep() ).isRequired();
    }


    private void addStepAct() {
        Component actPanel = getStep().isActionStep()
                ? new ActionStepDataPanel( "act", stepData, getFinder() )
                : getStep().isCommunicationStep()
                ? new CommunicationStepDataPanel( "act", stepData, getFinder() )
                : getStep().isReceiptConfirmation()
                ? new ReceiptConfirmationDataPanel( "act", stepData, getFinder() )
                : new SubTaskStepDataPanel( "act", stepData, getFinder() );
        actPanel.add( new AttributeModifier( "class", getStep().isRequired() ? "required" : "optional" ) );
        stepContainer.add( actPanel );
    }

    private Label makeLocalActStep() {
        Label actLabel = new Label( "act", getStep().getLabel() );
        actLabel.add( new AttributeModifier( "class", "step-act" ) );
        return actLabel;
    }

    private void addConstraints() {
        constraintsContainer = new WebMarkupContainer( "constraintsContainer" );
        constraintsContainer.setVisible( isConstrained( getStep() ) );
        stepContainer.add( constraintsContainer );
        addIfGuards();
        addUnlessGuards();
        addPrerequisiteSteps();
    }

    private boolean isConstrained( Step step ) {
        return getChecklist().hasGuards( step, POSITIVE )
                || getChecklist().hasGuards( step, !POSITIVE )
                || getChecklist().hasPrerequisites( step );
    }

    private void addIfGuards() {
        WebMarkupContainer ifsContainer = new WebMarkupContainer( "ifsContainer" );
        ifsContainer.setVisible( getChecklist().hasGuards( getStep(), POSITIVE ) );
        constraintsContainer.add( ifsContainer );
        List<StepGuard> ifGuards = getIfGuards();
        ListView<StepGuard> ifListView = new ListView<StepGuard>(
                "ifs",
                ifGuards
        ) {
            @Override
            protected void populateItem( ListItem<StepGuard> item ) {
                final StepGuard stepGuard = item.getModelObject();
                Condition condition = getChecklist().deRefCondition( stepGuard.getConditionRef() );
                String conditionString = condition.getLabel();
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

    @SuppressWarnings("unchecked")
    private List<StepGuard> getIfGuards() {
        return (List<StepGuard>) CollectionUtils.select(
                getChecklist().getStepGuards(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (StepGuard) object ).isPositive();
                    }
                }
        );
    }

    private void addUnlessGuards() {
        List<StepGuard> unlessGuards = getUnlessGuards();
        WebMarkupContainer unlessesContainer = new WebMarkupContainer( "unlessesContainer" );
        unlessesContainer.setVisible( getChecklist().hasGuards( getStep(), !POSITIVE ) );
        constraintsContainer.add( unlessesContainer );
        ListView<StepGuard> unlessListView = new ListView<StepGuard>(
                "unlesses",
                unlessGuards
        ) {
            @Override
            protected void populateItem( ListItem<StepGuard> item ) {
                final StepGuard stepGuard = item.getModelObject();
                Condition condition = getChecklist().deRefCondition( stepGuard.getConditionRef() );
                String conditionString = condition.getLabel();
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
        return (List<StepGuard>) CollectionUtils.select(
                getChecklist().getStepGuards(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return !( (StepGuard) object ).isPositive();
                    }
                }
        );
    }

    private void addPrerequisiteSteps() {
        List<StepOrder> stepOrders = getStepOrders();
        WebMarkupContainer aftersContainer = new WebMarkupContainer( "aftersContainer" );
        aftersContainer.setVisible( getChecklist().hasPrerequisites( getStep() ) );
        constraintsContainer.add( aftersContainer );
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


    private List<StepOrder> getStepOrders() {
        return getChecklist().listStepOrdersFor( getStep() );
    }

    private Checklist getChecklist() {
        return part.getChecklist();
    }

    private Step getStep() {
        return stepData.getStep();
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
