package com.mindalliance.channels.social.services;

import com.mindalliance.channels.core.community.PlanCommunity;
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
     * @param planCommunity    a plan community
     * @param rfiSurvey a survey
     * @return a list of rfis
     */
    List<RFI> select( PlanCommunity planCommunity, RFISurvey rfiSurvey );

    /**
     * Get the number of RFIs based on a given questionnaire.
     *
     * @param planCommunity    a plan community
     * @param questionnaire a questionnaire
     * @return an int
     */
    int getRFICount( PlanCommunity planCommunity, Questionnaire questionnaire );

    /**
     * Add or update an RFI.
     *
     * @param planCommunity    a plan community
     * @param username     who adds or updates
     * @param rfiSurvey    a survey
     * @param userInfo     user info
     * @param organization an organization
     * @param title        a string
     * @param role         a role
     * @param deadlineDate a date or null if no deadline
     */
    void makeOrUpdateRFI(
            PlanCommunity planCommunity,
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
     * @param planCommunity    a plan community
     * @param username  who nags
     * @param rfiSurvey a survey
     * @param userInfo  user info
     */
    void nag(
            PlanCommunity planCommunity,
            String username,
            RFISurvey rfiSurvey,
            ChannelsUserInfo userInfo );

    /**
     * Find matching RFI RFI.
     *
     * @param planCommunity    a plan community
     * @param surveyedUsername who is being surveyed
     * @param rfiSurvey        a survey
     */
    RFI find(
            PlanCommunity planCommunity,
            RFISurvey rfiSurvey,
            String surveyedUsername );

    /**
     * Find the usernames of all participants in a survey.
     *
     * @param planCommunity    a plan community
     * @param rfiSurvey a survey
     * @return a list of strings
     */
    List<String> findParticipants( PlanCommunity planCommunity, RFISurvey rfiSurvey );


    /**
     * Find all active RFIs for a user in a given plan.
     *
     * @param planCommunity    a plan community
     * @return a list of RFIs
     */
    List<RFI> listActiveRFIs( PlanCommunity planCommunity );

    /**
     * Find all active RFIs for a user in a given plan.
     *
     * @param planCommunity    a plan community
     * @param user         a user
     * @return a list of RFIs
     */
    List<RFI> listUserActiveRFIs( PlanCommunity planCommunity, ChannelsUser user );

    /**
     * Find all RFIs sent to a given user that are ongoing in a given plan.
     *
     * @param planCommunity    a plan community
     * @param user         a user
     * @return a list of RFIs
     */
    List<RFI> listOngoingUserRFIs( PlanCommunity planCommunity, ChannelsUser user );

    /**
     * Toggle declining an RFI.
     *
     * @param rfi an rfi
     * @param reason a string
     */
    void toggleDecline( RFI rfi, String reason );

    /**
     * Find all rfis for which planners have requested nagging.
     * @param planCommunity    a plan community
     * @return a list of RFIs
     */
    List<RFI> listRequestedNags( PlanCommunity planCommunity );

    /**
     * Find all RFIs for which an approaching deadline notification needs to be sent.
     * @param planCommunity    a plan community
     * @param warningDelay a long - msecs until deadline triggering warning
     * @return a list of RFIs
     */
    List<RFI> listApproachingDeadline( PlanCommunity planCommunity, long warningDelay );

    /**
     * Find all RFIs of which surveyed user has yet to be notified.
     * @param planCommunity    a plan community
     * @return a list of RFIs
     */
    List<RFI> listNewRFIs( PlanCommunity planCommunity );
}
