package com.mindalliance.channels.api.procedures.checklist;

import com.mindalliance.channels.api.procedures.CycleData;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.checklist.Step;
import com.mindalliance.channels.core.model.checklist.SubTaskStep;
import com.mindalliance.channels.core.model.time.Cycle;

/**
 * Web service data for a sub-task collaboration step in a checklist.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/8/13
 * Time: 8:35 PM
 */
public abstract class SubTaskStepData extends AbstractStepData {

    private CycleData cycleData;


    public SubTaskStepData() {
        // required
    }

    public SubTaskStepData( Step step,
                            ChecklistData checklist,
                            String serverUrl,
                            CommunityService communityService,
                            ChannelsUser user ) {
        super( step, checklist, serverUrl, communityService, user );
        initCycle();
    }

    private void initCycle() {
        Cycle cycle = getSubTaskStep().getSharing().getCycle();
        if ( cycle != null ) {
            cycleData = new CycleData( cycle );
        }
    }

    public boolean isResearchStep() {
        return getSubTaskStep().isResearch();
    }

    public boolean isFollowUpStep() {
        return getSubTaskStep().isFollowUp();
    }

    public CycleData getCycle() {
        return cycleData;
    }

    protected SubTaskStep getSubTaskStep() {
        return (SubTaskStep) getStep();
    }

}
