package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.model.Event;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.Level;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Phase;
import com.mindalliance.channels.model.Segment;

import java.util.ArrayList;
import java.util.List;

/**
 * Plan segment might not terminate.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 10, 2009
 * Time: 3:01:54 PM
 */
public class SegmentNeverEnds extends AbstractIssueDetector {

    public SegmentNeverEnds() {
    }

    /**
     * {@inheritDoc}
     */
    public boolean canBeWaived() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    protected String getLabel() {
        return "Segment's event phase may never end";
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Segment segment = (Segment) modelObject;
        Phase phase = segment.getPhase();
        Event event = segment.getEvent();
        if ( !( phase.isConcurrent() && event.isSelfTerminating()
                || phase.isPreEvent() && getPlan().isIncident( event ) )
                && getQueryService().findTerminators( segment ).isEmpty() ) {
            Issue issue = makeIssue( Issue.COMPLETENESS, segment );
            issue.setDescription( "The segment's event phase may never come to an end." );
            issue.setRemediation( "If the segment is for a co-event phase, have the event end on its own"
                    + "\nor if the segment is for a pre-event phase, have the event be an incident"
                    + "\nor if the segment is for a pre-event phase, have a task in another segment cause it"
                    + "\nor have at least one task in the plan segment terminate it." );
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
}
