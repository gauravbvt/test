package com.mindalliance.channels.engine.analysis.detectors;

import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;
import com.mindalliance.channels.core.model.Assignment;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.engine.query.Assignments;

import java.util.ArrayList;
import java.util.List;

/**
 * An organization expected to be assigned tasks has no agent (in it or its sub-organizations)
 * assigned to a category of task.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Sep 9, 2010
 * Time: 3:05:23 PM
 */
public class OrganizationWithNoAssignmentToCategoryOfTask extends AbstractIssueDetector {

    public OrganizationWithNoAssignmentToCategoryOfTask() {
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Organization org = (Organization) modelObject;
        if ( getQueryService().isInvolvementExpected( org ) ) {
            Assignments assignments = getQueryService().getAssignments().with( org );

            if ( !assignments.isEmpty() ) {
                for ( Part.Category category : Part.Category.values() ) {
                    if ( !isCategoryCovered( assignments, category ) ) {
                        Issue issue = makeIssue( Issue.COMPLETENESS, org );
                        issue.setSeverity( Level.Low );
                        issue.setDescription( "Organization \""
                                + org.getName()
                                + "\" is not assigned any "
                                + category.getLabel().toLowerCase()
                                + " task." );
                        issue.setRemediation( "Specify at least one "
                                + category.getLabel().toLowerCase()
                                + " task that will be assigned to organization \""
                                + org.getName()
                                + "\"." );
                        issues.add( issue );
                    }
                }
            }
        }
        return issues;
    }

    private boolean isCategoryCovered( Assignments assignments, Part.Category category ) {
        for ( Assignment assignment : assignments ) {
            Part.Category partCategory = assignment.getPart().getCategory();
            if ( partCategory != null && partCategory.equals( category ) )
                return true;
        }
        return false;
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
        return "Organization not assigned a category of tasks";
    }

    /**
     * {@inheritDoc}
     */
    public boolean canBeWaived() {
        return true;
    }


}
