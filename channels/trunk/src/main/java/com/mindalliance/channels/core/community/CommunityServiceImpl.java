package com.mindalliance.channels.core.community;

import com.mindalliance.channels.core.community.participation.Agent;
import com.mindalliance.channels.core.community.participation.UserParticipation;
import com.mindalliance.channels.core.community.participation.UserParticipationConfirmationService;
import com.mindalliance.channels.core.community.participation.UserParticipationService;
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
    private final UserParticipationService userParticipationService;
    private UserParticipationConfirmationService userParticipationConfirmationService;

    public CommunityServiceImpl(
            PlanCommunity planCommunity,
            PlanService planService,
            Analyst analyst,
            UserParticipationService userParticipationService,
            UserParticipationConfirmationService userParticipationConfirmationService ) {
        this.planCommunity = planCommunity;
        this.planService = planService;
        this.analyst = analyst;
        this.userParticipationService = userParticipationService;
        this.userParticipationConfirmationService = userParticipationConfirmationService;
    }

    @Override
    public UserParticipationService getUserParticipationService() {
        return userParticipationService;
    }

    @Override
    public UserParticipationConfirmationService getUserParticipationConfirmationService() {
        return userParticipationConfirmationService;
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
        List<UserParticipation> participations = userParticipationService.getParticipationsAsAgent( new Agent( actor ), planCommunity );
        ChannelsUserDao userDao = planService.getUserDao();
        for ( UserParticipation participation : participations ) {
            if ( !actor.isSupervisedParticipation()
                    || userParticipationConfirmationService.isConfirmedByAllSupervisors( participation, planCommunity ) ) {
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
                                                 List<UserParticipation> activeParticipations ) {
        if ( !actor.isParticipationRestrictedToEmployed() ) return true;
        List<Organization> actorEmployers = planService.findDirectAndIndirectEmployers(
                planService.findAllEmploymentsForActor( actor ) );
        List<Organization> myPlannedEmployers = new ArrayList<Organization>();
        for ( UserParticipation participation : activeParticipations ) {
            Actor participationActor = participation.getAgent( planCommunity ).getActor( );
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
