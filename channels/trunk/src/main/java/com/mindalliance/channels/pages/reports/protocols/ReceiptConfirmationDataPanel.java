package com.mindalliance.channels.pages.reports.protocols;

import com.mindalliance.channels.api.procedures.checklist.ChecklistStepData;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.checklist.ReceiptConfirmationStep;

/**
 * Receipt Confirmation Data Panel.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 7/7/13
 * Time: 12:14 PM
 */
public class ReceiptConfirmationDataPanel extends ChecklistStepDataPanel {

    public ReceiptConfirmationDataPanel( String id, Part part, ChecklistStepData stepData, int index, ProtocolsFinder finder ) {
        super( id, part, stepData, index, finder );
    }

    @Override
    protected String getInstructions() {
        return "";
    }

    @Override
    protected String getStepAct() {
        return getReceiptConfirmationStep().getLabel();
    }

    private ReceiptConfirmationStep getReceiptConfirmationStep() {
        return (ReceiptConfirmationStep) getStep();
    }

}
