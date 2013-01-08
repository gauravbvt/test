package com.mindalliance.channels.core.community.participation.issues;

import com.mindalliance.channels.core.community.PlanCommunity;

import java.util.List;

/**
 * Participation analyst.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 1/8/13
 * Time: 9:11 AM
 */
public interface ParticipationAnalyst {

    List<ParticipationIssue> detectAllIssues( PlanCommunity planCommunity );

}
