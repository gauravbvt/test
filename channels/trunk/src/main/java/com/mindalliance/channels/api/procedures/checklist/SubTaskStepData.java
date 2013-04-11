package com.mindalliance.channels.api.procedures.checklist;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.checklist.Step;
import com.mindalliance.channels.core.model.checklist.SubTaskStep;

/**
 * Web service data for a sub-task collaboration step in a checklist.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/8/13
 * Time: 8:35 PM
 */
public abstract class SubTaskStepData extends AbstractStepData {


    public SubTaskStepData() {
        // required
    }

    public SubTaskStepData( Step step,
                            ChecklistData checklist,
                            String serverUrl,
                            CommunityService communityService,
                            ChannelsUser user ) {
        super( step, checklist, serverUrl, communityService, user );
    }

    public boolean isResearchStep() {
        return getSubTaskStep().isResearch();
    }

    public boolean isFollowUpStep() {
        return getSubTaskStep().isFollowUp();
    }

    protected SubTaskStep getSubTaskStep() {
        return (SubTaskStep) getStep();
    }

}
