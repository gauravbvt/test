package com.mindalliance.channels.pages.reports.protocols;

import com.mindalliance.channels.api.procedures.AssignmentData;
import com.mindalliance.channels.api.procedures.FollowUpData;
import com.mindalliance.channels.api.procedures.ResearchData;
import com.mindalliance.channels.api.procedures.checklist.ChecklistStepData;
import com.mindalliance.channels.api.procedures.checklist.FollowUpStepData;
import com.mindalliance.channels.api.procedures.checklist.ResearchStepData;
import com.mindalliance.channels.api.procedures.checklist.SubTaskStepData;
import com.mindalliance.channels.core.model.checklist.SubTaskStep;
import org.apache.wicket.markup.html.basic.Label;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/9/13
 * Time: 6:28 PM
 */
public class SubTaskStepDataPanel extends AbstractDataPanel {

    private final ChecklistStepData stepData;

    public SubTaskStepDataPanel( String id, ChecklistStepData stepData, ProtocolsFinder finder ) {
        super( id, finder );
        this.stepData = stepData;
        init();
    }

    private void init() {
        addRequired();
        addDo();
        addChecklistLink();
    }

    private void addRequired() {
        Label requiredLabel = new Label( "required", getStep().isRequired() ? " - Required" : " - Optional" );
        requiredLabel.setVisible( getStep().isRequired() );
        add( requiredLabel );
    }

    private void addDo() {
        String label = getStep().isResearch() ? "Research \"" : "Follow up with \"";
        label += getStep().getSharing().getName();
        label += "\"";
        add( new Label( "do", label ) );
    }

    private void addChecklistLink() {
        add( new ChecklistDataLinkPanel( "checklistLink", getSubTaskAssignmentData(), getFinder() ) );
    }

    private AssignmentData getSubTaskAssignmentData() {
        return getStep().isFollowUp()
                ? getFollowUpData().getFollowUpAssignment()
                : getResearchData().getResearchAssignment();
    }

    private SubTaskStepData getSubTaskStepData() {
        return getStep().isResearch()
                ? stepData.getResearchStep()
                : stepData.getFollowUpStep();
    }

    private SubTaskStep getStep() {
        return (SubTaskStep)stepData.getStep();
    }

    private FollowUpData getFollowUpData() {
        return ((FollowUpStepData)getSubTaskStepData()).getFollowUp();
    }

    private ResearchData getResearchData() {
        return ((ResearchStepData)getSubTaskStepData()).getResearch();
    }


}
