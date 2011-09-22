package com.mindalliance.channels.engine.analysis.detectors;

import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.engine.query.QueryService;

import java.util.ArrayList;
import java.util.List;

/**
 * A defined task is triggered but the triggering task is never started.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 10, 2009
 * Time: 1:04:03 PM
 */
public class TriggeredButNeverStartedDefinedTask extends AbstractIssueDetector {

    public TriggeredButNeverStartedDefinedTask() {
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> detectIssues( QueryService queryService, ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Part part = (Part) modelObject;
        if ( !part.hasDefaultTask() ) {
            if ( !part.isStartsWithSegment() ) {
                if ( part.isTriggered() ) {
                    boolean started = queryService.findIfPartStarted( part );
                    if ( !started ) {
                        Issue issue = makeIssue( queryService, Issue.COMPLETENESS, part );
                        issue.setDescription( "This task is triggered but any flow that triggers it"
                                + "  comes from a task that is itself never started." );
                        issue.setRemediation( "Make sure a triggering flow comes from a task that is started." );
                        issue.setSeverity( queryService.computePartPriority( part ) );
                        issues.add( issue );
                    }
                }
            } else {
                boolean started = queryService.findIfSegmentStarted( part.getSegment() );
                if ( !started ) {
                    Issue issue = makeIssue( queryService, Issue.COMPLETENESS, part );
                    issue.setDescription( "This task starts with the plan segment but no task"
                            + " is ever started that causes the plan segment to happen.");
                    issue.setRemediation( "Make sure the event phase of the segment can start " +
                            "(it is a co-event phase of an incident " +
                            "or the phase is started by a task that itself can start)" );
                    issue.setSeverity( queryService.computePartPriority( part ) );
                    issues.add( issue );
                }
            }
        }
        return issues;
    }

    /**
     * {@inheritDoc}
     */
    public boolean appliesTo( ModelObject modelObject ) {
        return modelObject instanceof Part;
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
        return "Task is triggered but never started";
    }
}
