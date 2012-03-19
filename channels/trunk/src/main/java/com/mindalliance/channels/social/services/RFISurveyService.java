package com.mindalliance.channels.social.services;

import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.orm.service.GenericSqlService;
import com.mindalliance.channels.social.model.rfi.RFI;
import com.mindalliance.channels.social.model.rfi.RFISurvey;

import java.util.List;

/**
 * RFI survey service.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/7/12
 * Time: 11:16 AM
 */
public interface RFISurveyService extends GenericSqlService<RFISurvey, Long> {

    /**
     * Get all surveys for a plan, possibly restricting to open ones about a given of model object.
     *
     *
     * @param plan a plan
     * @param onlyOpen    a boolean
     * @param aboutTypeName a string
     * @return a list of surveys
     */
    List<RFISurvey> select( Plan plan, boolean onlyOpen, String aboutTypeName );

    /**
     * Find response metrics for a survey.
     * @param rfiSurvey a survey
     * @param rfiService the rfi service
     * @return a string like "105c 95i 3d" (105 completed, 95 incomplete 3 declined)
     */
    String findResponseMetrics( RFISurvey rfiSurvey, RFIService rfiService );

    /**
     * Find names of all users participating in a survey.
     * @param rfiSurvey a survey
     * @return a list of strings
     */
    List<String> findParticipants( RFISurvey rfiSurvey );

    /**
     * Find the RFI of a user in a survey, if participating.
     * @param username a string
     * @param rfiSurvey  a survey
     * @return  an RFI or null
     */
    RFI findRFI( String username, RFISurvey rfiSurvey );
}
