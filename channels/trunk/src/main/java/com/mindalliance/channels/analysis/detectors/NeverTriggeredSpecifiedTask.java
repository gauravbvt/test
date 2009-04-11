package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.analysis.DetectedIssue;
import com.mindalliance.channels.Issue;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.Part;

import java.util.List;
import java.util.ArrayList;

/**
 * A part with a defined task is never triggered.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 10, 2009
 * Time: 11:24:52 AM
 */
public class NeverTriggeredSpecifiedTask extends AbstractIssueDetector {
    public NeverTriggeredSpecifiedTask() {
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Part part = (Part) modelObject;
        if ( !part.hasDefaultTask() && !part.isStartsWithScenario() && !part.isTriggered() ) {
                Issue issue = new DetectedIssue( Issue.STRUCTURAL, part );
                issue.setDescription( "This task is never triggered  even though"
                        + " it does not start with the scenario." );
                issue.setRemediation( "Start this task with the scenario,"
                        + " or have an incoming flow trigger it" );
                issue.setSeverity( Issue.Level.Major );
                issues.add( issue );
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
