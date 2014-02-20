package com.mindalliance.channels.pages.reports.protocols;

import com.mindalliance.channels.api.procedures.checklist.ChecklistStepData;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.checklist.ActionStep;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/10/13
 * Time: 1:00 PM
 */
public class ActionStepDataPanel extends ChecklistStepDataPanel {

    public ActionStepDataPanel( String id,
                                Part part,
                                ChecklistStepData stepData,
                                int index,
                                ProtocolsFinder finder,
                                boolean allExpanded ) {
        super( id, part, stepData, index, finder, allExpanded );
    }

    @Override
    protected String getInstructions() {
        return getActionStep().getInstructions();
    }

    @Override
    protected String getStepAct() {
        return getActionStep().getAction();
    }

    private ActionStep getActionStep() {
        return (ActionStep)getStep();
    }

}
