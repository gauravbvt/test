package com.mindalliance.channels.engine.analysis.detectors.collaborationTemplate;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.EventTiming;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;

import java.util.ArrayList;
import java.util.List;

/**
 * Redundant event timing found in segment context.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 1/12/11
 * Time: 10:59 AM
 */
public class RedundantSegmentContext extends AbstractIssueDetector {

    public RedundantSegmentContext() {
    }


    @Override
    public List<Issue> detectIssues( CommunityService communityService, Identifiable modelObject ) {
        QueryService queryService = communityService.getModelService();
        List<Issue> issues = new ArrayList<Issue>(  );
        Segment segment = (Segment)modelObject;
        List<EventTiming> context = segment.getContext();
        for ( EventTiming eventTiming : context ) {
            for ( EventTiming other : context ) {
                if ( !eventTiming.equals( other ) ) {
                    if ( eventTiming.implies( other, queryService.getPlanLocale() ) ) {
                        Issue issue = makeIssue( communityService, Issue.COMPLETENESS, segment );
                        issue.setDescription( "Context \"" + eventTiming + "\" implies \"" + other + "\"." );
                        issue.setRemediation( "Remove  \""
                                + eventTiming
                                + "\" from the context \n or remove \""
                                + other
                                + "\"." );
                        issue.setSeverity( Level.Low );
                        issues.add( issue );
                    }
                }
            }
        }
        return issues;
    }

    @Override
    public boolean appliesTo( Identifiable modelObject ) {
        return modelObject instanceof Segment;
    }

    @Override
    public String getTestedProperty() {
        return null;
    }

    @Override
    protected String getKindLabel() {
        return "Redundancy in segment context";
    }

}
