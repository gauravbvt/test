package com.mindalliance.channels.engine.analysis.detectors;

import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Job;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 9/30/13
 * Time: 10:06 AM
 */
public class OrganizationHasManyPrimaryJobsForSameActor extends AbstractIssueDetector {

    public OrganizationHasManyPrimaryJobsForSameActor() {
    }

    @Override
    public boolean appliesTo( ModelObject modelObject ) {
        return modelObject instanceof Organization
                && ( (Organization) modelObject ).isActual();
    }

    @Override
    public List<? extends Issue> detectIssues( QueryService queryService, ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Organization org = (Organization) modelObject;
        Map<Actor, Integer> primaryJobsCount = new HashMap<Actor, Integer>();
        for ( Job job : org.getJobs() ) {
            if ( job.isPrimary() ) {
                Actor actor = job.getActor();
                if ( !primaryJobsCount.keySet().contains( actor ) ) {
                    primaryJobsCount.put( actor, 1 );
                } else {
                    primaryJobsCount.put( actor, primaryJobsCount.get( actor ) + 1 );
                }
            }
        }
        for ( Actor actor : primaryJobsCount.keySet() ) {
            int count = primaryJobsCount.get( actor );
            if ( count > 1 ) {
                Issue issue = makeIssue( queryService, Issue.VALIDITY, org );
                issue.setDescription( "Agent " + actor.getName() + " has " + count + " primary jobs in "
                        + org.getName() + ". The maximum number allowed is one." );
                issue.setRemediation( "Mark all but one of the agent's jobs as linked jobs." );
                issue.setSeverity( Level.Medium );
                issues.add( issue );
            }
        }
        return issues;
    }

    @Override
    public String getTestedProperty() {
        return null;
    }

    @Override
    protected String getKindLabel() {
        return "Organization has more than one primary (hiring) job for a given agent";
    }
}
