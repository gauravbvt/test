package com.mindalliance.channels.social.services;

import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.orm.service.GenericSqlService;
import com.mindalliance.channels.social.model.rfi.RFI;
import com.mindalliance.channels.social.model.rfi.RFIForward;
import com.mindalliance.channels.social.model.rfi.RFISurvey;

import java.util.List;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/13/12
 * Time: 2:10 PM
 */
public interface RFIForwardService extends GenericSqlService<RFIForward, Long> {
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
     * Find all forwards in a survey.
     *
     * @param plan      a plan
     * @param rfiSurvey a survey
     * @return a list of forwards
     */
    List<RFIForward> select( Plan plan, RFISurvey rfiSurvey );
}
