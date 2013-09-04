package com.mindalliance.channels.engine.analysis.detectors;

import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Job;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;
import org.apache.wicket.util.collections.ArrayListStack;

import java.util.List;

/**
 * Agent employed without a title.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/16/12
 * Time: 10:53 AM
 */
public class JobWithoutTitle extends AbstractIssueDetector {

    public JobWithoutTitle() {
    }

    @Override
    public List<? extends Issue> detectIssues( QueryService queryService, ModelObject modelObject ) {
        List<Issue> issues = new ArrayListStack<Issue>();
        Organization org = (Organization) modelObject;
        for ( Job job : org.getJobs() ) {
            if ( job.getTitle().isEmpty() ) {
                Issue issue = makeIssue( queryService, Issue.COMPLETENESS, org );
                issue.setDescription( "Agent \""
                        + job.getActorName()
                        + "\" is employed by \""
                        + org.getName()
                        + "\" as \""
                        + job.getRoleName()
                        + "\" without a title." );
                issue.setRemediation( "Give a title for the job \nor remove the job." );
                issue.setSeverity( Level.Low );
                issues.add( issue );
            }
        }
        return issues;
    }

    @Override
    public boolean appliesTo( ModelObject modelObject ) {
        return modelObject instanceof Organization
                && ( (Organization) modelObject ).isActual();
    }

    @Override
    public String getTestedProperty() {
        return null;
    }

    @Override
    protected String getKindLabel() {
        return "Agent employed without a title";
    }
}
