package com.mindalliance.channels.api.procedures.checklist;

import com.mindalliance.channels.api.procedures.GoalData;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.checklist.GoalAchievedOutcome;
import com.mindalliance.channels.core.model.checklist.Outcome;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 7/25/13
 * Time: 12:59 PM
 */
@XmlType(name = "goalOutcome", propOrder = {"label", "goal"})
public class GoalAchievedOutcomeData extends OutcomeData {

    private GoalData goal;

    public GoalAchievedOutcomeData() {
        // required
    }

    public GoalAchievedOutcomeData( Outcome outcome,
                                    String serverUrl,
                                    CommunityService communityService,
                                    ChannelsUser user ) {
        super( outcome, serverUrl, communityService, user );
    }

    @Override
    protected void initData( Outcome outcome,
                             String serverUrl,
                             CommunityService communityService,
                             ChannelsUser user ) {
        super.initData( outcome, serverUrl, communityService, user );
        goal = new GoalData( ( (GoalAchievedOutcome) getOutcome() ).getGoal() );
    }

    @Override
    @XmlElement
    public String getLabel() {
        return super.getLabel();
    }

    @XmlElement
    public GoalData getGoal() {
        return goal;
    }

 }
