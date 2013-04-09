package com.mindalliance.channels.api.procedures.checklist;

import com.mindalliance.channels.api.procedures.ResearchData;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.checklist.Step;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

/**
 * Web service data for a research collaboration step in a checklist.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/8/13
 * Time: 8:34 PM
 */
@XmlType( name = "researchStep", propOrder = {"label", "research", "ifConditions", "unlessConditions", "prerequisites"} )
public class ResearchStepData extends SubTaskStepData {

    private ResearchData research;

    public ResearchStepData() {
        // required
    }

    public ResearchStepData( Step step,
                             ChecklistData checklist,
                             String serverUrl,
                             CommunityService communityService,
                             ChannelsUser user ) {
        super( step, checklist, serverUrl, communityService, user );
    }

    @Override
    protected void initData( String serverUrl, CommunityService communityService, ChannelsUser user ) {
        super.initData( serverUrl, communityService, user );
        research = new ResearchData(
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
    public ResearchData getResearch() {
        return research;
    }
}
