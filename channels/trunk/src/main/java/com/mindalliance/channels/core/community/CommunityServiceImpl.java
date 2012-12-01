package com.mindalliance.channels.core.community;

import com.mindalliance.channels.core.community.participation.PlanParticipation;
import com.mindalliance.channels.core.community.participation.PlanParticipationService;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.dao.user.ChannelsUserDao;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.query.PlanService;
import com.mindalliance.channels.engine.analysis.Analyst;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Community service implementation.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 11/29/12
 * Time: 3:24 PM
 */
public class CommunityServiceImpl implements CommunityService {

    private PlanCommunity planCommunity;
    private final PlanService planService;
    private Analyst analyst;
    private final PlanParticipationService planParticipationService;

    public CommunityServiceImpl(
            PlanCommunity planCommunity,
            PlanService planService,
            Analyst analyst,
            PlanParticipationService planParticipationService ) {
        this.planCommunity = planCommunity;
        this.planService = planService;
        this.analyst = analyst;
        this.planParticipationService = planParticipationService;
    }

    @Override
    public PlanParticipationService getPlanParticipationService() {
        return planParticipationService;
    }

    @Override
    public PlanService getPlanService() {
        return planService;
    }

    @Override
    public Analyst getAnalyst() {
        return analyst;
    }

    @Override
    public List<ChannelsUser> findUsersParticipatingAs( Actor actor ) {
        Set<ChannelsUser> users = new HashSet<ChannelsUser>();
        List<PlanParticipation> participations = planParticipationService.getParticipationsAsActor( actor, planCommunity );
        ChannelsUserDao userDao = planService.getUserDao();
        for ( PlanParticipation participation : participations ) {
            if ( !actor.isSupervisedParticipation()
                    || planParticipationService.isValidatedByAllSupervisors( participation, planCommunity ) ) {
                ChannelsUser user = userDao.getUserNamed( participation.getParticipant().getUsername() );
                if ( user != null ) {
                    users.add( user );
                }
            }
        }
        return new ArrayList<ChannelsUser>( users );
    }

    @Override
    public boolean meetsPreEmploymentConstraint( Actor actor,
                                                 List<PlanParticipation> activeParticipations ) {
        if ( !actor.isParticipationRestrictedToEmployed() ) return true;
        List<Organization> actorEmployers = planService.findDirectAndIndirectEmployers(
                planService.findAllEmploymentsForActor( actor ) );
        List<Organization> myPlannedEmployers = new ArrayList<Organization>();
        for ( PlanParticipation participation : activeParticipations ) {
            Actor participationActor = participation.getActor( planService );
            if ( participationActor != null && !participationActor.isOpenParticipation() )
                myPlannedEmployers.addAll( planService.findDirectAndIndirectEmployers(
                        planService.findAllEmploymentsForActor( participationActor ) ) );
        }
        return !Collections.disjoint( myPlannedEmployers, actorEmployers );
    }




    private Plan getPlan() {
        return planService.getPlan();
    }

}
