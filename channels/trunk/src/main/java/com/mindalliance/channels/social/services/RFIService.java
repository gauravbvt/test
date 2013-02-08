package com.mindalliance.channels.social.services;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.dao.user.ChannelsUserInfo;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Role;
import com.mindalliance.channels.core.orm.service.GenericSqlService;
import com.mindalliance.channels.social.model.rfi.Questionnaire;
import com.mindalliance.channels.social.model.rfi.RFI;
import com.mindalliance.channels.social.model.rfi.RFISurvey;

import java.util.Date;
import java.util.List;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/16/12
 * Time: 2:19 PM
 */
public interface RFIService extends GenericSqlService<RFI, Long> {

    /**
     * List all RFIs in a given survey.
     *
     * @param communityService    a plan community service
     * @param rfiSurvey a survey
     * @return a list of rfis
     */
    List<RFI> select( CommunityService communityService, RFISurvey rfiSurvey );

    /**
     * Get the number of RFIs based on a given questionnaire.
     *
     * @param communityService    a plan community service
     * @param questionnaire a questionnaire
     * @return an int
     */
    int getRFICount( CommunityService communityService, Questionnaire questionnaire );

    /**
     * Add or update an RFI.
     *
     * @param communityService    a plan community service
     * @param username     who adds or updates
     * @param rfiSurvey    a survey
     * @param userInfo     user info
     * @param organization an organization
     * @param title        a string
     * @param role         a role
     * @param deadlineDate a date or null if no deadline
     */
    void makeOrUpdateRFI(
            CommunityService communityService,
            String username,
            RFISurvey rfiSurvey,
            ChannelsUserInfo userInfo,
            Organization organization,
            String title,
            Role role,
            Date deadlineDate );

    /**
     * Nag a user to complete an RFI.
     *
     * @param communityService    a plan community service
     * @param username  who nags
     * @param rfiSurvey a survey
     * @param userInfo  user info
     */
    void nag(
            CommunityService communityService,
            String username,
            RFISurvey rfiSurvey,
            ChannelsUserInfo userInfo );

    /**
     * Find matching RFI RFI.
     *
     * @param communityService    a plan community service
     * @param surveyedUsername who is being surveyed
     * @param rfiSurvey        a survey
     */
    RFI find(
            CommunityService communityService,
            RFISurvey rfiSurvey,
            String surveyedUsername );

    /**
     * Find the usernames of all participants in a survey.
     *
     * @param communityService    a plan community service
     * @param rfiSurvey a survey
     * @return a list of strings
     */
    List<String> findParticipants( CommunityService communityService, RFISurvey rfiSurvey );


    /**
     * Find all active RFIs for a user in a given plan.
     *
     * @param communityService    a plan community service
     * @return a list of RFIs
     */
    List<RFI> listActiveRFIs( CommunityService communityService );

    /**
     * Find all active RFIs for a user in a given plan.
     *
     * @param communityService    a plan community service
     * @param user         a user
     * @return a list of RFIs
     */
    List<RFI> listUserActiveRFIs( CommunityService communityService, ChannelsUser user );

    /**
     * Find all RFIs sent to a given user that are ongoing in a given plan.
     *
     * @param communityService    a plan community service
     * @param user         a user
     * @return a list of RFIs
     */
    List<RFI> listOngoingUserRFIs( CommunityService communityService, ChannelsUser user );

    /**
     * Toggle declining an RFI.
     *
     * @param rfi an rfi
     * @param reason a string
     */
    void toggleDecline( RFI rfi, String reason );

    /**
     * Find all rfis for which planners have requested nagging.
     * @param communityService    a plan community service
     * @return a list of RFIs
     */
    List<RFI> listRequestedNags( CommunityService communityService );

    /**
     * Find all RFIs for which an approaching deadline notification needs to be sent.
     * @param communityService    a plan community service
     * @param warningDelay a long - msecs until deadline triggering warning
     * @return a list of RFIs
     */
    List<RFI> listApproachingDeadline( CommunityService communityService, long warningDelay );

    /**
     * Find all RFIs of which surveyed user has yet to be notified.
     * @param communityService    a plan community service
     * @return a list of RFIs
     */
    List<RFI> listNewRFIs( CommunityService communityService );
}
