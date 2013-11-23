package com.mindalliance.channels.engine.analysis.detectors;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Employment;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Job;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;

import java.util.ArrayList;
import java.util.List;


/**
 * An actor in a job is directly supervised by someone he/she directly or indirectly supervises.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 17, 2010
 * Time: 10:09:05 AM
 */
public class CircularSupervision extends AbstractIssueDetector {

    public CircularSupervision() {
    }


    /**
     * {@inheritDoc}
     */
    public List<Issue> detectIssues( CommunityService communityService, Identifiable modelObject ) {
        QueryService queryService = communityService.getPlanService();
        Organization organization = (Organization) modelObject;
        List<Issue> issues = new ArrayList<Issue>();
        for ( Job job : organization.getJobs() ) {
            Actor supervisor = job.getSupervisor();
            if ( supervisor != null ) {
                List<Employment> allSupervised = queryService.findAllSupervisedBy( job.getActor() );
                for ( Employment employment : allSupervised ) {
                    Actor supervised = employment.getActor();
                    if ( supervised.equals( supervisor ) ) {
                        Issue issue = makeIssue( communityService, Issue.VALIDITY, organization );
                        issue.setDescription(
                                job.getLabel()
                                        + " directly or indirectly supervizes "
                                        + supervised.getName()
                                        + " and is directly supervised by same."
                        );
                        issue.setRemediation(
                                "Change supervisor for " + job.getLabel()
                                        + "\nor or remove "
                                        + job.getActor().getName()
                                        + " from the supervisory hierarchy of "
                                        + job.getSupervisor().getName()
                        );
                        issue.setSeverity( Level.Low );
                        issues.add( issue );
                    }
                }
            }
        }
        return issues;
    }

    /**
     * {@inheritDoc}
     */
    public boolean appliesTo( Identifiable modelObject ) {
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
        return "Circular supervisor chain";
    }

    /**
     * {@inheritDoc}
     */
    public boolean canBeWaived() {
        return true;
    }
}
