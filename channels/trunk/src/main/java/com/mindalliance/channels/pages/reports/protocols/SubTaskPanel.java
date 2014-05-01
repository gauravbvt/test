package com.mindalliance.channels.pages.reports.protocols;

import com.mindalliance.channels.api.procedures.AssignmentData;
import com.mindalliance.channels.api.procedures.CycleData;
import com.mindalliance.channels.api.procedures.FollowUpData;
import com.mindalliance.channels.api.procedures.ResearchData;
import com.mindalliance.channels.api.procedures.checklist.ChecklistStepData;
import com.mindalliance.channels.api.procedures.checklist.FollowUpStepData;
import com.mindalliance.channels.api.procedures.checklist.ResearchStepData;
import com.mindalliance.channels.api.procedures.checklist.SubTaskStepData;
import com.mindalliance.channels.core.model.checklist.Step;
import com.mindalliance.channels.core.model.checklist.SubTaskStep;
import org.apache.wicket.markup.html.basic.Label;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 10/23/13
 * Time: 2:11 PM
 */
public class SubTaskPanel extends AbstractDataPanel {

    private ChecklistStepData stepData;

    public SubTaskPanel( String id, ChecklistStepData stepData, ProtocolsFinder finder ) {
        super( id, finder );
        this.stepData = stepData;
        init();
    }

    private void init() {
        addCycle();
        addSubTaskLink();
    }

    private void addCycle() {
        CycleData cycleData = getSubTaskStepData().getCycle();
        Label cycleLabel = new Label( "cycle", cycleData == null ? "" : cycleData.getLabel() );
        add( cycleLabel );
    }

    private void addSubTaskLink() {
        add( new ChecklistDataLinkPanel( "subTaskLink", getSubTaskAssignmentData(), getFinder() ) );
    }

    private AssignmentData getSubTaskAssignmentData() {
        return getSubTaskStep().isFollowUp()
                ? getFollowUpData().getFollowUpAssignment()
                : getResearchData().getResearchAssignment();
    }

    private SubTaskStepData getSubTaskStepData() {
        return getSubTaskStep().isResearch()
                ? getStepData().getResearchStep()
                : getStepData().getFollowUpStep();
    }

    private SubTaskStep getSubTaskStep() {
        return (SubTaskStep)getStep();
    }

    private FollowUpData getFollowUpData() {
        return ((FollowUpStepData)getSubTaskStepData()).getFollowUp();
    }

    private ResearchData getResearchData() {
        return ((ResearchStepData)getSubTaskStepData()).getResearch();
    }

    private ChecklistStepData getStepData() {
        return stepData;
    }

    private Step getStep() {
        return stepData.getStep();
    }
}
