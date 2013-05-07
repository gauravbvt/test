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
 * Unsupervised job issue detector.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 17, 2010
 * Time: 9:22:11 AM
 */
public class UnsupervisedJob extends AbstractIssueDetector {

    public UnsupervisedJob() {
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> detectIssues( QueryService queryService, ModelObject modelObject ) {
        Organization organization = (Organization) modelObject;
        List<Issue> issues = new ArrayList<Issue>();
        for ( Job job : organization.getJobs() ) {
            if ( job.getSupervisor() == null ) {
                Issue issue = makeIssue( queryService, Issue.COMPLETENESS, organization );
                issue.setDescription( "No supervisor for " + job.getLabel() + "." );
                issue.setRemediation( "Add a supervisor to the job\nor remove the job" );
                issue.setSeverity( Level.Low );
                issues.add( issue );
            }
        }
        return issues;
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
        return "Unsupervised job";
    }

    /**
     * {@inheritDoc}
     */
    public boolean canBeWaived() {
        return true;
    }
}