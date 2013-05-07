package com.mindalliance.channels.engine.analysis.detectors;

import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Job;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.query.QueryService;

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
    protected String getKindLabel() {
        return "Unconfirmed job of agent in organization";
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> detectIssues( QueryService queryService, ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Organization org = (Organization) modelObject;
        for ( Job job : queryService.findUnconfirmedJobs( org ) ) {
            Issue issue = makeIssue( queryService, Issue.COMPLETENESS, org );
            issue.setDescription( "Job " + job + " is implied from the plan and not confirmed." );
            issue.setRemediation( "Confirm the job in the profile of " + org.getName() + "." );
            issue.setSeverity( Level.Low );
            issues.add( issue );
        }
        return issues;
    }
}