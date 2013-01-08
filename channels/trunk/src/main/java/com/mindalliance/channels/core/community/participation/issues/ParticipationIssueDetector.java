package com.mindalliance.channels.core.community.participation.issues;

import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.model.Identifiable;

import java.util.List;

/**
 * Participation issue detector.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 1/8/13
 * Time: 9:19 AM
 */
public interface ParticipationIssueDetector {

    boolean appliesTo( Identifiable identifiable );

    String getKind();

    List<ParticipationIssue> detectIssues( Identifiable identifiable, PlanCommunity planCommunity );
}
