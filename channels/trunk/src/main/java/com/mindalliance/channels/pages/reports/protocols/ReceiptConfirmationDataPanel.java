package com.mindalliance.channels.pages.reports.protocols;

import com.mindalliance.channels.api.procedures.checklist.ChecklistStepData;
import com.mindalliance.channels.core.model.checklist.ReceiptConfirmationStep;
import org.apache.wicket.markup.html.basic.Label;

/**
 * Receipt Confirmation Data Panel.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 7/7/13
 * Time: 12:14 PM
 */
public class ReceiptConfirmationDataPanel extends AbstractDataPanel {

    private final ChecklistStepData stepData;

    public ReceiptConfirmationDataPanel( String id, ChecklistStepData stepData, ProtocolsFinder finder ) {
        super( id, finder );
        this.stepData = stepData;
        init();
    }

    private void init() {
        addRequired();
        addDo();
    }

    private void addRequired() {
        Label requiredLabel = new Label( "required", getStep().isRequired() ? " - Required" : " - Optional" );
        requiredLabel.setVisible( getStep().isRequired() );
        add( requiredLabel );
    }

    private void addDo() {
        add( new Label( "do", getStep().getLabel() ) );
    }

    private ReceiptConfirmationStep getStep() {
        return (ReceiptConfirmationStep) stepData.getStep();
    }

}
