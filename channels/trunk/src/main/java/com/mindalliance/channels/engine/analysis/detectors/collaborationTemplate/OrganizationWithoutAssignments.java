package com.mindalliance.channels.engine.analysis.detectors.collaborationTemplate;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.query.Assignments;
import com.mindalliance.channels.core.query.QueryService;

import java.util.ArrayList;
import java.util.List;

/**
 * An organization which is expected to have task assignments has none.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Sep 10, 2010
 * Time: 11:24:36 AM
 */
public class OrganizationWithoutAssignments extends AbstractIssueDetector {

    public OrganizationWithoutAssignments() {
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> detectIssues( CommunityService communityService, Identifiable modelObject ) {
        QueryService queryService = communityService.getPlanService();
        List<Issue> issues = new ArrayList<Issue>();
        Organization org = (Organization) modelObject;
        if ( queryService.isInvolvementExpected( org ) ) {
            Assignments assignments = queryService.getAssignments().with( org );
            if ( assignments.isEmpty() ) {
                Issue issue = makeIssue( communityService, Issue.COMPLETENESS, org );
                issue.setSeverity( Level.Low );
                issue.setDescription( "Organization \""
                        + org.getName()
                        + "\" is not assigned any task but was expected to." );
                issue.setRemediation( "Remove the task assignment expectation on the organization" +
                        "\nor add a task that will be assigned to the organization"
                        + " or one of its sub-organizations, if any." );
                issues.add( issue );
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
        return "Organization without task assignments";
    }

}
