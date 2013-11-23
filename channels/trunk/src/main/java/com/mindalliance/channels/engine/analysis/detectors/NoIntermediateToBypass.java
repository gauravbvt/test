package com.mindalliance.channels.engine.analysis.detectors;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;

import java.util.ArrayList;
import java.util.List;

/**
 * Flow set to allow bypassing intermediate but there is none.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 9/23/11
 * Time: 12:37 PM
 */
public class NoIntermediateToBypass extends AbstractIssueDetector {
    @Override
    public List<Issue> detectIssues( CommunityService communityService, Identifiable modelObject ) {
        List<Issue> issues = new ArrayList<Issue>(  );
        Flow flow = (Flow)modelObject;
        if ( flow.isSharing() && flow.isCanBypassIntermediate() ) {
            List<Part> intermediated = flow.intermediatedTargets();
            if ( intermediated.isEmpty() ) {
                Issue issue = makeIssue( communityService, Issue.COMPLETENESS, flow );
                issue.setDescription( "Bypassing of intermediate is allowed but there is none." );
                issue.setRemediation( "Disallow bypassing of intermediate"
                        + "\nor make \""
                        + ( (Part) flow.getTarget() ).getTitle()
                        + "\" an intermediate by having it re-send \""
                        + flow.getName()
                        + "\"."
                );
                issue.setSeverity( Level.Low );
                issues.add( issue );
            }
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
        return "No intermediate to bypass";
    }
}
