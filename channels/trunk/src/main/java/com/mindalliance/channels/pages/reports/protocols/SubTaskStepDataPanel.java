package com.mindalliance.channels.pages.reports.protocols;

import com.mindalliance.channels.api.procedures.checklist.ChecklistStepData;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.checklist.SubTaskStep;
import org.apache.wicket.Component;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/9/13
 * Time: 6:28 PM
 */
public class SubTaskStepDataPanel extends ChecklistStepDataPanel {

    public SubTaskStepDataPanel( String id,
                                 Part part,
                                 ChecklistStepData stepData,
                                 int index,
                                 ProtocolsFinder finder,
                                 boolean allExpanded ) {
        super( id, part, stepData, index, finder, allExpanded );
    }

    @Override
    protected String getInstructions() {
        return "";
    }

    @Override
    protected String getStepAct() {
        String label = getSubTaskStep().isResearch() ? "Research \"" : "Follow up on \"";
        label += getSubTaskStep().getSharing().getName();
        label += "\"";
        return label;
    }

    @Override
    protected boolean hasMore() {
        return true;
    }

    @Override
    protected Component makeStepDetailsPanel( String id ) {
        return new SubTaskPanel( id, getStepData(), getFinder() );
    }


    private SubTaskStep getSubTaskStep() {
        return (SubTaskStep)getStep();
    }

}
