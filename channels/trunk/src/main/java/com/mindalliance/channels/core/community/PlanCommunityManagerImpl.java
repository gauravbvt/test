package com.mindalliance.channels.core.community;

import com.mindalliance.channels.core.community.participation.ParticipationManager;
import com.mindalliance.channels.core.dao.PlanManager;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.query.PlanService;
import com.mindalliance.channels.core.query.PlanServiceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Plan community manager.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 11/30/12
 * Time: 3:03 PM
 */
public class PlanCommunityManagerImpl implements PlanCommunityManager, ApplicationContextAware {

    private ApplicationContext applicationContext;
    private Map<Plan,PlanCommunity> planCommunities = new HashMap<Plan,PlanCommunity>();

    @Autowired
    private PlanManager planManager;

    @Autowired
    private PlanServiceFactory planServiceFactory;

    @Autowired
    private ParticipationManager participationManager;

    public PlanCommunityManagerImpl( ) {

    }

    @Override
    public synchronized void setApplicationContext( ApplicationContext applicationContext ) {
        this.applicationContext = applicationContext;
    }

    public PlanCommunity getPlanCommunityFor( Plan plan ) {    // todo - COMMUNITY - change that plan implies plan community
        PlanCommunity planCommunity = planCommunities.get( plan );
        if ( planCommunity == null ) {
            CommunityService communityService = (CommunityService)applicationContext.getBean( "communityService" ); // prototype bean
            communityService.setPlanService( getPlanService( plan ) );
            planCommunity = new PlanCommunity( communityService, participationManager );
            communityService.setPlanCommunity( planCommunity);
            planCommunities.put( plan, planCommunity );
        }
        return planCommunity;
    }

    @Override
    public PlanCommunity findPlanCommunity( String uri ) {       // todo - COMMUNITY - only assume one implied community per plan when modeling
        Plan plan = planManager.findProductionPlan( uri );
        return plan == null ? null : getPlanCommunityFor( plan );
    }

    @Override
    public PlanCommunity findPlanCommunity( String uri, int planVersion ) { // todo - COMMUNITY - don't assume one implied community per plan
        Plan plan = planManager.getPlan( uri, planVersion );
        return plan == null ? null : getPlanCommunityFor( plan );
    }

    @Override
    public List<PlanCommunity> getPlanCommunities() {
        List<PlanCommunity> planCommunities = new ArrayList<PlanCommunity>(  );
        for ( Plan plan : planManager.getPlans() ) {
             planCommunities.add( getPlanCommunityFor( plan ) );
        }
        return planCommunities;
    }


    private PlanService getPlanService( Plan plan ) { // todo create and return a subclass of PlanService that returns the community's locale if set else the plan's locale
        return planServiceFactory.getService( plan );
    }


}
