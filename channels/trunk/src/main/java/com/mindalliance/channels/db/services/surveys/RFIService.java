package com.mindalliance.channels.db.services.surveys;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Role;
import com.mindalliance.channels.db.data.surveys.AnswerSet;
import com.mindalliance.channels.db.data.surveys.Question;
import com.mindalliance.channels.db.data.surveys.Questionnaire;
import com.mindalliance.channels.db.data.surveys.RFI;
import com.mindalliance.channels.db.data.surveys.RFIForward;
import com.mindalliance.channels.db.data.surveys.RFISurvey;
import com.mindalliance.channels.db.data.users.UserRecord;
import com.mindalliance.channels.db.services.DataService;

import java.util.Date;
import java.util.List;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/30/13
 * Time: 3:27 PM
 */
public interface RFIService extends DataService<RFI> {

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
            UserRecord userInfo,
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
            UserRecord userInfo );

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

    /**
     * A count of how many have answered this question.
     *
     * @param question a question
     * @return an int
     */
    int getAnswerCount( Question question );

    /**
     * Find the answer set in an RFI to a question.
     *
     * @param rfi      an RFI
     * @param question a question
     * @return an answer set
     */
    AnswerSet findAnswerSet( RFI rfi, Question question );

    /**
     * Find all answer sets for an RFI.
     *
     * @param rfi an RFI
     * @return a list of answer sets
     */
    List<AnswerSet> selectAnswerSets( RFI rfi );

    /**
     * Find usernames of who forwarded the RFI.
     *
     * @param rfi an RFI
     * @return a string
     */
    List<String> findForwarderUsernames( RFI rfi );

    /**
     * Find emails of who were forwarded the RFI.
     *
     * @param rfi an RFI
     * @return a string
     */
    List<String> findForwardedTo( RFI rfi );


    /**
     * Save answers and delete those marked for removal. Save answerSet.
     *
     * @param answerSet an answer set
     * @param rfi an rfi
     * @param question  a question
     */
    void saveAnswerSet( AnswerSet answerSet, RFI rfi, Question question );

    List<RFIForward> selectForwards( CommunityService communityService, RFISurvey rfiSurvey );

    void saveRFIForward( RFIForward forward );

    List<RFIForward> findForwardsTo( String email, RFISurvey rfiSurvey );
}
