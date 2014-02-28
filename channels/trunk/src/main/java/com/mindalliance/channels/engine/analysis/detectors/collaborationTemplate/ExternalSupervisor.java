/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.engine.analysis.detectors.collaborationTemplate;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Job;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import java.util.ArrayList;
import java.util.List;

/**
 * A job's supervisor is neither from same organization nor from parent organization.
 */
public class ExternalSupervisor extends AbstractIssueDetector {

    public ExternalSupervisor() {
    }

    @Override
    public List<Issue> detectIssues( CommunityService communityService, Identifiable modelObject ) {
        QueryService queryService = communityService.getModelService();
        Organization organization = (Organization) modelObject;
        List<Issue> issues = new ArrayList<Issue>();
        for ( Job job : organization.getJobs() ) {
            if ( hasExternalSupervisor( job, organization, queryService ) ) {
                Issue issue = makeIssue( communityService, Issue.VALIDITY, organization );
                issue.setDescription( job.getLabel() + " is supervized by " + job.getSupervisor().getName()
                                      + " who works for an external organization." );
                issue.setRemediation( "Transfer the supervisor to " + organization.getName() + " or its parent (if any)"
                                      + "\nor transfer the job to the supervisor's organization or child organization (if any)" );
                issue.setSeverity( Level.Low );
                issues.add( issue );
            }
        }
        return issues;
    }

    private static boolean hasExternalSupervisor( Job job, final Organization organization,
                                                  QueryService queryService ) {
        Actor supervisor = job.getSupervisor();
        final Organization parent = organization.getParent();
        if ( supervisor == null ) {
            return false;
        } else {
            List<Organization> supervisorOrgs = queryService.findEmployers( supervisor );
            return !CollectionUtils.exists( supervisorOrgs, new Predicate() {
                @Override
                public boolean evaluate( Object object ) {
                    Organization org = (Organization) object;
                    return org.equals( organization ) || parent != null && org.equals( parent );
                }
            } );
        }
    }

    @Override
    public boolean appliesTo( Identifiable modelObject ) {
        return modelObject instanceof Organization;
    }

    @Override
    public String getTestedProperty() {
        return null;
    }

    @Override
    protected String getKindLabel() {
        return "An agent's supervisor is from another organization";
    }

    @Override
    public boolean canBeWaived() {
        return true;
    }
}
