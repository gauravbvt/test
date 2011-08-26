package com.mindalliance.channels.engine.analysis.detectors;

import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Job;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Organization;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import java.util.ArrayList;
import java.util.List;

/**
 * A job's supervisor is neither from same organization nor from parent organization.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 17, 2010
 * Time: 9:43:16 AM
 */
public class ExternalSupervisor extends AbstractIssueDetector {

    public ExternalSupervisor() {
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> detectIssues( ModelObject modelObject ) {
        Organization organization = (Organization) modelObject;
        List<Issue> issues = new ArrayList<Issue>();
        for ( Job job : organization.getJobs() ) {
            if ( hasExternalSupervisor( job, organization ) ) {
                Issue issue = makeIssue( Issue.VALIDITY, organization );
                issue.setDescription(
                        job.getLabel()
                                + " is supervized by "
                                + job.getSupervisor().getName()
                                + " who works for an external organization." );
                issue.setRemediation(
                        "Transfer the supervisor to " + organization.getName() + " or its parent (if any)"
                                + "\nor transfer the job to the supervisor's organization or child organization (if any)"
                );
                issue.setSeverity( Level.Low );
                issues.add( issue );
            }
        }
        return issues;
    }

    private boolean hasExternalSupervisor( Job job, final Organization organization ) {
        Actor supervisor = job.getSupervisor();
        final Organization parent = organization.getParent();
        if ( supervisor == null ) {
            return false;
        } else {
            List<Organization> supervisorOrgs = getQueryService().findEmployers( supervisor );
            return !CollectionUtils.exists(
                    supervisorOrgs,
                    new Predicate() {
                        public boolean evaluate( Object object ) {
                            Organization org = (Organization) object;
                            return org.equals( organization )
                                    || (parent != null && org.equals( parent ));
                        }
                    }
            );
        }
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
        return "External supervisor";
    }

    /**
     * {@inheritDoc}
     */
    public boolean canBeWaived() {
        return true;
    }
}
