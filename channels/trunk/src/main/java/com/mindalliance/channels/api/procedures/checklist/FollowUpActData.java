package com.mindalliance.channels.api.procedures.checklist;

import com.mindalliance.channels.api.procedures.FollowUpData;
import com.mindalliance.channels.core.community.CommunityService;
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
@XmlType( name = "followUpStep", propOrder = {"label", "followUp", "ifConditions", "unlessConditions", "prerequisites"} )
public class FollowUpActData  extends SubTaskStepData {

    private FollowUpData followUp;

    public FollowUpActData() {
        // required
    }
    public FollowUpActData( Step step,
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
                getChecklist().getAssignment(),
                user
        );
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

    @XmlElement
    public FollowUpData getFollowUp() {
        return followUp;
    }

    public Part getDiscoveringPart() {
        return (Part)getSubTaskStep().getSharing().getSource();
    }

}
