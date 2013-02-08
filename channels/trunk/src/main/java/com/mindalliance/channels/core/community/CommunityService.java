package com.mindalliance.channels.core.community;

import com.mindalliance.channels.core.community.participation.Agency;
import com.mindalliance.channels.core.community.participation.OrganizationParticipationService;
import com.mindalliance.channels.core.community.participation.ParticipationAnalyst;
import com.mindalliance.channels.core.community.participation.ParticipationManager;
import com.mindalliance.channels.core.community.participation.UserParticipation;
import com.mindalliance.channels.core.community.participation.UserParticipationConfirmationService;
import com.mindalliance.channels.core.community.participation.UserParticipationService;
import com.mindalliance.channels.core.community.protocols.CommunityAssignments;
import com.mindalliance.channels.core.community.protocols.CommunityCommitments;
import com.mindalliance.channels.core.dao.AbstractModelObjectDao;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.dao.user.ChannelsUserDao;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.query.PlanService;
import com.mindalliance.channels.engine.analysis.Analyst;

import java.util.Date;
import java.util.List;

/**
 * Community service.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 11/30/12
 * Time: 8:56 AM
 */
public interface CommunityService {

    UserParticipationService getUserParticipationService();

    UserParticipationConfirmationService getUserParticipationConfirmationService();

    OrganizationParticipationService getOrganizationParticipationService();

    PlanService getPlanService();

    void setPlanService( PlanService planService );

    Analyst getAnalyst();

    AbstractModelObjectDao getDao();

    void setPlanCommunity( PlanCommunity planCommunity );

    <T extends ModelObject> T find( Class<T> clazz, long id, Date dateOfRecord ) throws NotFoundException;

    boolean exists( Class<? extends ModelObject> clazz, Long id, Date dateOfRecord );

    boolean canHaveParentAgency( final String name, String parentName );

    List<Agency> findAncestors( String agencyName );

    boolean isCustodianOf( ChannelsUser user, Organization placeholder );

    /**
     * Find all users that participate a a given actor.
     *
     * @param actor an actor
     * @return a list of users
     */
    List<ChannelsUser> findUsersParticipatingAs( Actor actor );   // todo --obsolete

    /**
     * Whether participation as actor possible given current participation.
     * @param actor an actor
     * @param activeParticipations  a list of active participations
     * @return  a boolean -- not cached
     */
    Boolean meetsPreEmploymentConstraint( Actor actor, // todo -- obsolete
                                          List<UserParticipation> activeParticipations );

    CommunityCommitments getAllCommitments( Boolean includeToSelf );

    CommunityCommitments findAllCommitments( Flow flow, Boolean includeToSelf );

    CommunityAssignments getAllAssignments();

    CommunityCommitments findAllBypassCommitments( final Flow flow );

    void clearCache();

    void onDestroy();

    PlanCommunity getPlanCommunity();

    Plan getPlan();

    ParticipationManager getParticipationManager();

    ChannelsUserDao getUserDao();

    ParticipationAnalyst getParticipationAnalyst();
}
