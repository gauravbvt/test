package com.mindalliance.channels.api.procedures.checklist;

import com.mindalliance.channels.api.procedures.CycleData;
import com.mindalliance.channels.api.procedures.FollowUpData;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.protocols.CommunityAssignment;
import com.mindalliance.channels.core.community.protocols.CommunityCommitment;
import com.mindalliance.channels.core.community.protocols.CommunityCommitments;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.checklist.Step;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

/**
 * Web service data for a follow up collaboration step in a checklist.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/8/13
 * Time: 8:34 PM
 */
@XmlType( name = "followUpStep", propOrder = {"label", "cycle", "followUp", "ifConditions", "unlessConditions", "prerequisites", "outcomes"} )
public class FollowUpStepData extends SubTaskStepData {

    private FollowUpData followUp;

    public FollowUpStepData() {
        // required
    }
    public FollowUpStepData( Step step,
                             ChecklistData checklist,
                             String serverUrl,
                             CommunityService communityService,
                             ChannelsUser user ) {
        super( step, checklist, serverUrl, communityService, user );
    }

    @Override
    protected void initData( String serverUrl, CommunityService communityService, ChannelsUser user ) {
        super.initData( serverUrl, communityService, user );
        followUp = new FollowUpData(
                serverUrl,
                communityService,
                getSubTaskStep().getSharing(),
                getFollowUpAssignment( communityService ),
                user
        );
     }

    private CommunityAssignment getFollowUpAssignment( CommunityService communityService ) {
        CommunityCommitments allCommitments = communityService.findAllCommitments(
                getSubTaskStep().getSharing(),
                true
        );
        CommunityCommitments followUpCommitments = allCommitments.committing( getChecklist().getAssignment() );
        CommunityCommitment followUpCommitment = followUpCommitments.iterator().next();
        return followUpCommitment.getBeneficiary();
    }

    @Override
    @XmlElement( name = "if" )
    public List<ConditionData> getIfConditions() {
        return super.getIfConditions();
    }

    @Override
    @XmlElement
    public String getLabel() {
        return super.getLabel();
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
    public FollowUpData getFollowUp() {
        return followUp;
    }

    @XmlElement
    public CycleData getCycle() {
        return super.getCycle();
    }

    public Part getDiscoveringPart() {
        return (Part)getSubTaskStep().getSharing().getSource();
    }

}
