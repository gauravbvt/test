package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.model.Event;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.Level;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Phase;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.model.Segment;

import java.util.ArrayList;
import java.util.List;

/**
 * An initiated plan segment can never be caused according to the plan.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 11, 2009
 * Time: 2:52:56 PM
 */
public class SegmentNeverStarts extends AbstractIssueDetector {

    public SegmentNeverStarts() {
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Segment segment = (Segment) modelObject;
        Plan plan = getPlan();
        Event event = segment.getEvent();
        Phase phase = segment.getPhase();
        boolean isIncident = plan.isIncident( event );
        if ( !( isIncident && phase.isConcurrent()
                ||
                ( isIncident || !getQueryService().findCausesOf( event ).isEmpty() )
                        && event.isSelfTerminating() && phase.isPostEvent() )
                && !getQueryService().isInitiated( segment ) ) {
            Issue issue = makeIssue( Issue.COMPLETENESS, segment );
            issue.setDescription( "The plan segment may never start"
                    + " because it is not in response to an incident and no other plan segment causes it to start." );
            issue.setRemediation( "If the segment is for a co-event phase, have the event be an incident"
                    + "\nor if the segment is for a co-event phase, have a task in another segment cause it"
                    + "\nor if the segment is for a post-event phase, have the event be self-terminating"
                    + "\nor if the segment is for a post-event phase, "
                    + "have at least one task in a co-event segment terminate its segment." );
            issue.setRemediation( "Make this plan segment in response to an incident\n"
                    + "or make sure that a task in another plan segment causes this plan segment to start." );
            issue.setSeverity( Level.Medium );
            issues.add( issue );
        }
        return issues;
    }


    /**
     * {@inheritDoc}
     */
    public boolean appliesTo( ModelObject modelObject ) {
        return modelObject instanceof Segment;
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
        return "Segment's event phase may never start";
    }

    /**
      * {@inheritDoc}
      */
    public boolean canBeWaived() {
        return true;
    }
}
