package com.mindalliance.channels.social.services;

import com.mindalliance.channels.core.dao.user.ChannelsUserInfo;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Plan;
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
     * Are all required questions answered?
     *
     * @param rfi an RFI
     * @return a boolean
     */
    boolean isCompleted( RFI rfi );

    /**
     * Are some required questions unanswered?
     *
     * @param rfi an RFI
     * @return a boolean
     */
    boolean isIncomplete( RFI rfi );

    /**
     * List all RFIs in a given survey.
     *
     * @param rfiSurvey a survey
     * @return a list of rfis
     */
    List<RFI> select( RFISurvey rfiSurvey );

    /**
     * Get the number of RFIs based on a given questionnaire.
     *
     * @param questionnaire a questionnaire
     * @return an int
     */
    int getRFICount( Questionnaire questionnaire );

    /**
     * Add or update an RFI.
     * @param plan a plan
     * @param username who adds or updates
     * @param rfiSurvey a survey
     * @param userInfo user info
     * @param organization an organization
     * @param title a string
     * @param role a role
     * @param deadlineDate a date or null if no deadline
     */
    void makeOrUpdateRFI(
            Plan plan,
            String username,
            RFISurvey rfiSurvey,
            ChannelsUserInfo userInfo,
            Organization organization,
            String title,
            Role role,
            Date deadlineDate );

    /**
     * Nag a user to complete an RFI.
     * @param plan a plan
     * @param username who nags
     * @param rfiSurvey a survey
     * @param userInfo user info
     * @param organization an organization
     * @param title a string
     * @param role a role
     */
    void nag(
            Plan plan,
            String username,
            RFISurvey rfiSurvey,
            ChannelsUserInfo userInfo,
            Organization organization,
            String title,
            Role role );
}
