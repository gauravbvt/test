package com.mindalliance.channels.pages.reports.protocols;

import com.mindalliance.channels.api.procedures.AbstractFlowData;
import com.mindalliance.channels.api.procedures.checklist.ChecklistStepData;
import com.mindalliance.channels.api.procedures.checklist.CommunicationStepData;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.checklist.CommunicationStep;
import org.apache.wicket.Component;

/**
 * Communication step data panel.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/9/13
 * Time: 6:27 PM
 */
public class CommunicationStepDataPanel extends ChecklistStepDataPanel {

   public CommunicationStepDataPanel( String id, Part part, ChecklistStepData stepData, int index, ProtocolsFinder finder ) {
        super( id, part, stepData, index, finder );
    }

    @Override
    protected String getStepAct() {
        Flow.Intent intent = getCommunicationStep().getSharing().getIntent();
        String message = getCommunicationStep().getSharing().getName();
        if ( message == null ) message = "something";
        String label = getCommunicationStep().isNotification()
                ? "Send "
                : getCommunicationStep().isRequest()
                ? "Ask for "
                : "Answer with ";
        label += intent == null
                ? "information"
                : intent.getLabel().toLowerCase();
        label += " \"" + message + "\"";
        return label;
    }

    @Override
    protected String getInstructions() {
        return getFlowData().getInstructions();
    }

    @Override
    protected boolean hasMore() {
        return true;
    }

    @Override
    protected Component makeStepDetailsPanel( String id ) {
        return new CommitmentDataPanel(
                id,
                getFlowData(),
                !isReceived(),
                getFinder()
        );
    }

    private boolean isReceived() {
        return !getCommunicationStep().isRequest();
    }

    private AbstractFlowData getFlowData() {
        return getCommunicationStepData().getFlowData();
    }

    private CommunicationStepData getCommunicationStepData() {
        return getCommunicationStep().isRequest()
                ? getStepData().getRequestStep()
                : getCommunicationStep().isNotification()
                ? getStepData().getNotificationStep()
                : getStepData().getAnswerStep();
    }

    private CommunicationStep getCommunicationStep() {
        return (CommunicationStep) getStep();
    }

}
