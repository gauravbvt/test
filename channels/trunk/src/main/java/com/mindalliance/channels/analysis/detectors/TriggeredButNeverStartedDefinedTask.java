package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.analysis.DetectedIssue;
import com.mindalliance.channels.Issue;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.Part;

import java.util.List;
import java.util.ArrayList;

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
            if ( !part.isStartsWithScenario() ) {
                if ( part.isTriggered() ) {
                    boolean started = getDqo().findIfPartStarted( part );
                    if ( !started ) {
                        Issue issue = new DetectedIssue( Issue.STRUCTURAL, part );
                        issue.setDescription( "This task is triggered but any flow that triggers it"
                                + "  comes from a task that is itself never started." );
                        issue.setRemediation( "Make sure a triggering flow comes from a task that is started." );
                        issue.setSeverity( Issue.Level.Major );
                        issues.add( issue );
                    }
                }
            } else {
                boolean started = getDqo().findIfScenarioStarted( part.getScenario() );
                if ( !started ) {
                    Issue issue = new DetectedIssue( Issue.STRUCTURAL, part );
                    issue.setDescription( "This task starts with the scenario but no task"
                            + " is ever started that causes the scenario to happen.");
                    issue.setRemediation( "Make sure the scenario is caused by a task"
                            + " that can start (from another scenario),"
                            + " or make the scenario an incident (i.e. happens irrespective of the plan." );
                    issue.setSeverity( Issue.Level.Major );
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
}
