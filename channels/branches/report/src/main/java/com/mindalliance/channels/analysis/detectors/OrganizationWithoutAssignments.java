package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.Level;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.query.Assignments;

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
    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Organization org = (Organization) modelObject;
        if ( getQueryService().isInvolvementExpected( org ) ) {
            Assignments assignments = getQueryService().getAssignments().withSome( org );
            if ( assignments.isEmpty() ) {
                Issue issue = makeIssue( Issue.COMPLETENESS, org );
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
        return "Organization without task assignments";
    }

}
