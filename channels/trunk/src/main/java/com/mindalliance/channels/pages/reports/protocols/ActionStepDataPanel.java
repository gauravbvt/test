package com.mindalliance.channels.pages.reports.protocols;

import com.mindalliance.channels.api.procedures.checklist.ChecklistStepData;
import com.mindalliance.channels.core.model.checklist.ActionStep;
import org.apache.wicket.markup.html.basic.Label;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/10/13
 * Time: 1:00 PM
 */
public class ActionStepDataPanel extends AbstractDataPanel {

    private final ChecklistStepData stepData;

    public ActionStepDataPanel( String id, ChecklistStepData stepData, ProtocolsFinder finder ) {
        super( id, finder );
        this.stepData = stepData;
        init();
    }

    private void init() {
        addRequired();
        addDo();
        addInstructions();
    }

     private void addRequired() {
        Label requiredLabel = new Label( "required", getStep().isRequired() ? " - Required" : " - Optional" );
        requiredLabel.setVisible( getStep().isRequired() );
        add( requiredLabel );
    }

    private void addDo() {
        add( new Label( "do", getStep().getAction() ) );
    }

    private void addInstructions() {
        String instructions = getStep().getInstructions();
        Label instructionsLabel = new Label( "instructions", instructions );
        instructionsLabel.setVisible( !instructions.isEmpty() );
        add( instructionsLabel );
    }

    private ActionStep getStep() {
        return (ActionStep)stepData.getStep();
    }

}
