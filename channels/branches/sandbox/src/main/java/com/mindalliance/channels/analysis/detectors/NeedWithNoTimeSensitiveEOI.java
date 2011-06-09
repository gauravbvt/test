package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.Level;
import com.mindalliance.channels.model.ModelObject;

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
    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>(  );
        Flow flow = (Flow)modelObject;
        if ( flow.isNeed() && !flow.getEois().isEmpty() && !flow.isTimeSensitive() ) {
            Issue issue = makeIssue( Issue.COMPLETENESS, flow );
            issue.setDescription( "None of the needed information elements is time-sensitive" );
            issue.setRemediation( "Make at least one element of information time-sensitive" +
                    "\nor remove this information need." );
            issue.setSeverity( Level.Medium );
            issues.add( issue );
        }
        return issues;
    }

    @Override
    public boolean appliesTo( ModelObject modelObject ) {
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
