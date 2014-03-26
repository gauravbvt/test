package com.mindalliance.channels.api.procedures.checklist;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.checklist.ActionStep;
import com.mindalliance.channels.core.model.checklist.Step;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/1/13
 * Time: 9:25 PM
 */
@XmlType(name = "actionStep", propOrder = {"label", "uid", "action", "instructions", "ifConditions", "unlessConditions",
        "prerequisites", "outcomes", "assetProvisioning"})
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
        if ( ( (ActionStep) step ).getAssetProvisioning() != null )
            assetProvisioning = new AssetProvisioningData(
                    serverUrl,
                    checklist,
                    ( (ActionStep) step ).getAssetProvisioning(),
                    communityService,
                    user );
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
    @XmlElement(name = "if")
    public List<ConditionData> getIfConditions() {
        return super.getIfConditions();
    }

    @Override
    @XmlElement(name = "after")
    public List<Integer> getPrerequisites() {
        return super.getPrerequisites();
    }

    @Override
    @XmlElement(name = "unless")
    public List<ConditionData> getUnlessConditions() {
        return super.getUnlessConditions();
    }

    @Override
    @XmlElement(name = "outcome")
    public List<OutcomeData> getOutcomes() {
        return super.getOutcomes();
    }

    @XmlElement
    public AssetProvisioningData getAssetProvisioning() {
        return assetProvisioning;
    }

    public ActionStep getActionStep() {
        return (ActionStep) getStep();
    }

    @Override
    public Set<Long> allAssetIds() {
        Set<Long> ids = new HashSet<Long>(  );
        ids.addAll( super.allAssetIds() );
        if ( assetProvisioning != null ) {
            ids.addAll( assetProvisioning.allAssetIds() );
        }
        return ids;
    }

    @Override
    public Set<Long> allEventIds() {
        Set<Long> ids = new HashSet<Long>(  );
        ids.addAll( super.allEventIds() );
        if ( assetProvisioning != null ) {
            ids.addAll( assetProvisioning.allEventIds() );
        }
        return ids;
    }

    @Override
    public Set<Long> allActorIds() {
        Set<Long> ids = new HashSet<Long>(  );
        ids.addAll( super.allActorIds() );
        if ( assetProvisioning != null ) {
            ids.addAll( assetProvisioning.allActorIds() );
        }
        return ids;
    }


    @Override
    public Set<Long> allPlaceIds() {
        Set<Long> ids = new HashSet<Long>(  );
        ids.addAll( super.allPlaceIds() );
        if ( assetProvisioning != null ) {
            ids.addAll( assetProvisioning.allPlaceIds() );
        }
        return ids;
    }

 }
