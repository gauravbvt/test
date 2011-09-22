package com.mindalliance.channels.engine.analysis.detectors;

import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;
import com.mindalliance.channels.core.model.EventTiming;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.engine.query.QueryService;

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
    public List<Issue> detectIssues( QueryService queryService, ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>(  );
        Segment segment = (Segment)modelObject;
        List<EventTiming> context = segment.getContext();
        for ( EventTiming eventTiming : context ) {
            for ( EventTiming other : context ) {
                if ( !eventTiming.equals( other ) ) {
                    if ( eventTiming.implies( other, queryService.getPlan().getLocale() ) ) {
                        Issue issue = makeIssue( queryService, Issue.COMPLETENESS, segment );
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
    public boolean appliesTo( ModelObject modelObject ) {
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
