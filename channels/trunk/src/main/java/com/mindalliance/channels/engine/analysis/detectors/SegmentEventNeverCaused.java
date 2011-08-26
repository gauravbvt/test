package com.mindalliance.channels.engine.analysis.detectors;

import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;
import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.model.Segment;

import java.util.ArrayList;
import java.util.List;

/**
 * A segment is about an event that is never caused.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 11, 2009
 * Time: 2:41:55 PM
 */
public class SegmentEventNeverCaused extends AbstractIssueDetector {

    public SegmentEventNeverCaused() {
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Segment segment = (Segment) modelObject;
        Event event = segment.getEvent();
        Plan plan = getPlan();
        if ( !plan.isIncident( event )
                && getQueryService().findCausesOf( event ).isEmpty() ) {
            Issue issue = makeIssue( Issue.COMPLETENESS, segment );
            issue.setDescription( "The plan segment is about an event that may never be caused." );
            issue.setRemediation( "Make the event in question an incident\n"
                    +"or make sure at least one task in any segment causes it." );
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
    protected String getKindLabel() {
        return "Planned-for event may never be caused";
    }
}
