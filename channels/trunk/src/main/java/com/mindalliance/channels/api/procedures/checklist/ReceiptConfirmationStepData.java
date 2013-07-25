package com.mindalliance.channels.api.procedures.checklist;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.checklist.ReceiptConfirmationStep;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 7/7/13
 * Time: 11:48 AM
 */
@XmlType( name = "receiptConfirmationStep", propOrder = {"label", "ifConditions", "unlessConditions", "prerequisites", "outcomes"} )
public class ReceiptConfirmationStepData extends AbstractStepData {

    public ReceiptConfirmationStepData() {
        // required
    }

    public ReceiptConfirmationStepData(
            ReceiptConfirmationStep step,
            ChecklistData checklist,
            String serverUrl,
            CommunityService communityService,
            ChannelsUser user ) {
        super( step, checklist, serverUrl, communityService, user );
    }

    @Override
    @XmlElement
    public String getLabel() {
        return super.getLabel();
    }

    @Override
    @XmlElement( name = "if" )
    public List<ConditionData> getIfConditions() {
        return super.getIfConditions();
    }

    @Override
    @XmlElement( name = "after" )
    public List<Integer> getPrerequisites() {
        return super.getPrerequisites();
    }

    @Override
    @XmlElement( name = "unless" )
    public List<ConditionData> getUnlessConditions() {
        return super.getUnlessConditions();
    }

    @Override
    @XmlElement( name = "outcome" )
    public List<OutcomeData> getOutcomes() {
        return super.getOutcomes();
    }

    protected ReceiptConfirmationStep getReceiptConfirmationStep() {
        return (ReceiptConfirmationStep)getStep();
    }
}
