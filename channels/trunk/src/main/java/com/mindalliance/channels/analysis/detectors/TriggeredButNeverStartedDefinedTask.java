package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Part;

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
    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Part part = (Part) modelObject;
        if ( !part.hasDefaultTask() ) {
            if ( !part.setIsStartsWithSegment() ) {
                if ( part.isTriggered() ) {
                    boolean started = getQueryService().findIfPartStarted( part );
                    if ( !started ) {
                        Issue issue = makeIssue( Issue.COMPLETENESS, part );
                        issue.setDescription( "This task is triggered but any flow that triggers it"
                                + "  comes from a task that is itself never started." );
                        issue.setRemediation( "Make sure a triggering flow comes from a task that is started." );
                        issue.setSeverity( getQueryService().getPartPriority( part ) );
                        issues.add( issue );
                    }
                }
            } else {
                boolean started = getQueryService().findIfSegmentStarted( part.getSegment() );
                if ( !started ) {
                    Issue issue = makeIssue( Issue.COMPLETENESS, part );
                    issue.setDescription( "This task starts with the plan segment but no task"
                            + " is ever started that causes the plan segment to happen.");
                    issue.setRemediation( "Make sure the plan segment is caused by a task"
                            + " that can start (from another plan segment)\n"
                            + "or make the plan segment an incident (i.e. happens independently of the plan." );
                    issue.setSeverity( getQueryService().getPartPriority( part ) );
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
    protected String getLabel() {
        return "Task triggered by never starting task";
    }
}
