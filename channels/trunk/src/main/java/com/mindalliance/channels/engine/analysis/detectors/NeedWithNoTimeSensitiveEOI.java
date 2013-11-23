package com.mindalliance.channels.engine.analysis.detectors;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;

import java.util.ArrayList;
import java.util.List;

/**
 * Info need has EOIs but none that is time-sensitive.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 6/8/11
 * Time: 3:28 PM
 */
public class NeedWithNoTimeSensitiveEOI extends AbstractIssueDetector {

    public NeedWithNoTimeSensitiveEOI() {
    }

    @Override
    public List<Issue> detectIssues( CommunityService communityService, Identifiable modelObject ) {
        List<Issue> issues = new ArrayList<Issue>(  );
        Flow flow = (Flow)modelObject;
        if ( flow.isNeed() && !flow.getEffectiveEois().isEmpty() && !flow.isTimeSensitive() ) {
            Issue issue = makeIssue( communityService, Issue.COMPLETENESS, flow );
            issue.setDescription( "None of the needed information elements is time-sensitive" );
            issue.setRemediation( "Make at least one element of information time-sensitive" +
                    "\nor remove this information need." );
            issue.setSeverity( Level.Medium );
            issues.add( issue );
        }
        return issues;
    }

    @Override
    public boolean appliesTo( Identifiable modelObject ) {
        return modelObject instanceof Flow;
    }

    @Override
    public String getTestedProperty() {
        return null;
    }

    @Override
    protected String getKindLabel() {
        return "None of the needed elements of information is time-sensitive";
    }
}
