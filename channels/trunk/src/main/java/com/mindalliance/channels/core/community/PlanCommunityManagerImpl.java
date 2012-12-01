package com.mindalliance.channels.core.community;

import com.mindalliance.channels.core.community.participation.PlanParticipationService;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.query.PlanService;
import com.mindalliance.channels.core.query.PlanServiceFactory;
import com.mindalliance.channels.engine.analysis.Analyst;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Plan community manager.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 11/30/12
 * Time: 3:03 PM
 */
public class PlanCommunityManagerImpl implements PlanCommunityManager {

    @Autowired
    private PlanServiceFactory planServiceFactory;

    private Analyst analyst;
    private PlanParticipationService planParticipationService;

    public PlanCommunityManagerImpl( ) {

    }

    public void setAnalyst( Analyst analyst ) {
        this.analyst = analyst;
    }

    public void setPlanParticipationService( PlanParticipationService planParticipationService ) {
        this.planParticipationService = planParticipationService;
    }

    public PlanCommunity getPlanCommunity( Plan plan ) {
        return new PlanCommunity( getCommunityPlanService( plan ), analyst, planParticipationService );
    }

    private PlanService getCommunityPlanService( Plan plan ) { // todo create and return a subclass of PlanService that returns the community's locale if set else the plan's locale
        return planServiceFactory.getService( plan );
    }


}
