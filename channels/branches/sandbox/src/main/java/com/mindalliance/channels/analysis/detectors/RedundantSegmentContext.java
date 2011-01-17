package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.model.EventTiming;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.Level;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Segment;

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
    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>(  );
        Segment segment = (Segment)modelObject;
        List<EventTiming> context = segment.getContext();
        for ( EventTiming eventTiming : context ) {
            for ( EventTiming other : context ) {
                if ( !eventTiming.equals( other ) ) {
                    if ( eventTiming.implies( other, getPlan().getLocale() ) ) {
                        Issue issue = makeIssue( Issue.COMPLETENESS, segment );
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
    protected String getLabel() {
        return "Redundancy in segment context";
    }

}
