package com.mindalliance.channels.api.procedures.checklist;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.checklist.Outcome;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 7/25/13
 * Time: 12:52 PM
 */
public class OutcomeData implements Serializable {

    private Outcome outcome;

    public OutcomeData(  ) {
        // required;
    }

    public OutcomeData( Outcome outcome,
                          String serverUrl,
                          CommunityService communityService,
                          ChannelsUser user ) {
        initData( outcome, serverUrl, communityService, user );
    }

    protected void initData( Outcome outcome,
                             String serverUrl,
                             CommunityService communityService,
                             ChannelsUser user ) {
        this.outcome = outcome;
    }

    public String getLabel() {
        return outcome.getLabel();
    }

    public Outcome getOutcome() {
        return outcome;
    }

    public Set<Long> allEventIds() {
        return new HashSet<Long>(  );
    }

    public Set<Long> allPhaseIds() {
        return new HashSet<Long>(  );
    }

    public Set<Long> allActorIds() { // todo created info capability resource spec....
        return new HashSet<Long>(  );
    }

    public Set<Long> allRoleIds() {  // todo created info capability resource spec....
        return new HashSet<Long>(  );
    }

    public Set<Long> allPlaceIds() {   // todo created info capability resource spec....
        return new HashSet<Long>(  );
    }

    public Set<Long> allOrganizationIds() { // todo created info capability resource spec....
        return new HashSet<Long>(  );
    }

    public Set<Long> allInfoProductIds() { // todo created info capability resource spec....
        return new HashSet<Long>(  );
    }

    public Set<Long> allAssetIds() {
        return new HashSet<Long>();
    }


    public boolean isGoalAchievedOutcome() {
        return outcome.isGoalAchievedOutcome();
    }

    public boolean isEventTimingOutcome() {
        return outcome.isEventTimingOutcome();
    }

    public boolean isCapabilityCreatedOutcome() {
        return outcome.isCapabilityCreatedOutcome();
    }

    public boolean isAssetProducedOutcome() {
        return outcome.isAssetProducedOutcome();
    }
 }
