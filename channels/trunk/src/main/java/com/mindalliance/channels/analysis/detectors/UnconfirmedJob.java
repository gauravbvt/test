package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.analysis.DetectedIssue;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.Issue;
import com.mindalliance.channels.Organization;
import com.mindalliance.channels.Job;

import java.util.List;
import java.util.ArrayList;

/**
 * Organization has an unconfirmed job.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 26, 2009
 * Time: 3:18:56 PM
 */
public class UnconfirmedJob extends AbstractIssueDetector {

    /**
     * {@inheritDoc}
     */
    public boolean appliesTo( ModelObject modelObject ) {
        return modelObject instanceof Organization;
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
    protected List<Issue> doDetectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Organization org = (Organization) modelObject;
        for ( Job job : getService().findUnconfirmedJobs( org ) ) {
            Issue issue = new DetectedIssue( Issue.DEFINITION, org );
            issue.setDescription( "Job " + job + " is implied from scenarios and not confirmed." );
            issue.setRemediation( "Edit organization " + org + " and confirm the job (under \"details\")." );
            issue.setSeverity( Issue.Level.Minor );
            issues.add( issue );
        }
        return issues;
    }
}
