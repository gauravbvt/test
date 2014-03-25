package com.mindalliance.channels.api.procedures.checklist;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.checklist.ActionStep;
import com.mindalliance.channels.core.model.checklist.Step;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/1/13
 * Time: 9:25 PM
 */
@XmlType( name = "actionStep", propOrder = {"label", "uid", "action", "instructions", "ifConditions", "unlessConditions", "prerequisites", "outcomes", "assetProvisioning"} )
public class ActionStepData extends AbstractStepData {

    private AssetProvisioningData assetProvisioning;

    public ActionStepData() {
        // required
    }

    public ActionStepData( Step step,
                           ChecklistData checklist,
                           String serverUrl,
                           CommunityService communityService,
                           ChannelsUser user ) {
        super( step, checklist, serverUrl, communityService, user );
        assetProvisioning = new AssetProvisioningData(
                checklist,
                ((ActionStep)step).getAssetProvisioning(),
                communityService );
    }

    @Override
    @XmlElement
    public String getLabel() {
        return super.getLabel();
    }

    @XmlElement
    public String getUid() {
        return getActionStep().getUid();
    }

    @XmlElement
    public String getAction() {
        return getActionStep().getAction();
    }

    @XmlElement
    public String getInstructions() {
        return getActionStep().getInstructions();
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

    @XmlElement
    public AssetProvisioningData getAssetProvisioning() {
        return assetProvisioning;
    }

    public ActionStep getActionStep() {
        return (ActionStep)getStep();
    }
}
