package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.analysis.DetectedIssue;
import com.mindalliance.channels.model.Event;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.model.Severity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * No plan segment responds to an incident.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 13, 2009
 * Time: 10:28:42 AM
 */
public class NoSegmentRepondsToIncident extends AbstractIssueDetector {

    public NoSegmentRepondsToIncident() {
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Plan plan = (Plan) modelObject;
        for ( Event incident : plan.getIncidents() ) {
            boolean responded = false;
            Iterator<Segment> segments = getQueryService().list( Segment.class ).iterator();
            while ( !responded && segments.hasNext() ) {
                Segment segment = segments.next();
                if ( segment.getEvent().equals(incident) ) responded = true;
            }
            if ( !responded ) {
                DetectedIssue issue = makeIssue( Issue.COMPLETENESS, plan );
                issue.setDescription( "No plan segment responds to incident \"" + incident.getName() + "\"." );
                issue.setRemediation( "Define a plan segment that responds to it\n"
                        + "remove the event's incident status." );
                issue.setSeverity( ( Severity.Major ) );
                issues.add( issue );
            }
        }
        return issues;
    }

    /**
     * {@inheritDoc}
     */
    public boolean appliesTo( ModelObject modelObject ) {
        return modelObject instanceof Plan;
    }

    /**
     * {@inheritDoc}
     */
    public String getTestedProperty() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    protected String getLabel() {
        return "No plan segment for event";
    }
}
