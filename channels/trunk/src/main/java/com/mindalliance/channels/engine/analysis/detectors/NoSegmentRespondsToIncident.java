package com.mindalliance.channels.engine.analysis.detectors;

import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;
import com.mindalliance.channels.engine.analysis.DetectedIssue;
import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.model.Segment;

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
public class NoSegmentRespondsToIncident extends AbstractIssueDetector {

    public NoSegmentRespondsToIncident() {
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
                Event event = segment.getEvent();
                if ( event != null && event.equals(incident) ) responded = true;
            }
            if ( !responded ) {
                DetectedIssue issue = makeIssue( Issue.COMPLETENESS, plan );
                issue.setDescription( "No plan segment responds to incident \"" + incident.getName() + "\"." );
                issue.setRemediation( "Define a plan segment that responds to it\n"
                        + "remove the event's incident status." );
                issue.setSeverity( ( Level.High ) );
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
    protected String getKindLabel() {
        return "No plan segment for incident";
    }
}
