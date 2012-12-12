package com.mindalliance.channels.core.community;

import com.mindalliance.channels.core.community.participation.ParticipationManager;
import com.mindalliance.channels.core.community.participation.UserParticipationConfirmationService;
import com.mindalliance.channels.core.community.participation.UserParticipationService;
import com.mindalliance.channels.core.dao.PlanManager;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.query.PlanService;
import com.mindalliance.channels.core.query.PlanServiceFactory;
import com.mindalliance.channels.engine.analysis.Analyst;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

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

    @Autowired
    private PlanManager planManager;

    @Autowired
    private ParticipationManager participationManager;

    private Analyst analyst;
    private UserParticipationService userParticipationService;
    private UserParticipationConfirmationService userParticipationConfirmationService;

    public PlanCommunityManagerImpl( ) {

    }

    public void setAnalyst( Analyst analyst ) {
        this.analyst = analyst;
    }

    public void setUserParticipationService( UserParticipationService userParticipationService ) {
        this.userParticipationService = userParticipationService;
    }

    public void setUserParticipationConfirmationService(
            UserParticipationConfirmationService userParticipationConfirmationService ) {
        this.userParticipationConfirmationService = userParticipationConfirmationService;
    }

    public PlanCommunity makePlanCommunity( Plan plan ) {
        return new PlanCommunity(
                getPlanService( plan ),
                analyst,
                userParticipationService,
                userParticipationConfirmationService,
                participationManager );
    }

    @Override
    public PlanCommunity findPlanCommunity( String uri ) {       // todo - don't assume one implied community per plan
        Plan plan = planManager.findProductionPlan( uri );
        return plan == null ? null : makePlanCommunity( plan );
    }

    @Override
    public PlanCommunity findPlanCommunity( String uri, int planVersion ) { // todo - don't assume one implied community per plan
        Plan plan = planManager.getPlan( uri, planVersion );
        return plan == null ? null : makePlanCommunity( plan );
    }

    @Override
    public List<PlanCommunity> getPlanCommunities() {
        List<PlanCommunity> planCommunities = new ArrayList<PlanCommunity>(  );
        for ( Plan plan : planManager.getPlans() ) {
             planCommunities.add( makePlanCommunity( plan ) );
        }
        return planCommunities;
    }


    private PlanService getPlanService( Plan plan ) { // todo create and return a subclass of PlanService that returns the community's locale if set else the plan's locale
        return planServiceFactory.getService( plan );
    }


}
