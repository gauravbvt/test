package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.Job;
import com.mindalliance.channels.model.Level;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Organization;

import java.util.ArrayList;
import java.util.List;

/**
 * Organization has an unconfirmed job.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 26, 2009
 * Time: 3:18:56 PM
 */
public class UnconfirmedJob extends AbstractIssueDetector {

    public UnconfirmedJob() {
    }

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
    protected String getLabel() {
        return "Unconfirmed job";
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Organization org = (Organization) modelObject;
        for ( Job job : getQueryService().findUnconfirmedJobs( org ) ) {
            Issue issue = makeIssue( Issue.COMPLETENESS, org );
            issue.setDescription( "Job " + job + " is implied from the plan and not confirmed." );
            issue.setRemediation( "Edit organization " + org + " and confirm the job (under \"details\")." );
            issue.setSeverity( Level.Low );
            issues.add( issue );
        }
        return issues;
    }
}
