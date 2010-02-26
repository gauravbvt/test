package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.model.Assignment;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Severity;

import java.util.ArrayList;
import java.util.List;

/**
 * Part without assignment.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 10, 2009
 * Time: 12:58:17 PM
 */
public class UnplayedPart extends AbstractIssueDetector {

    public UnplayedPart() {
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> detectIssues( ModelObject modelObject ) {
        Part part = (Part) modelObject;
        List<Issue> issues = new ArrayList<Issue>();
        if ( part.hasNonActualActorResource() ) {
            List<Assignment> assignments = getQueryService().findAllAssignments( part, false );
            if ( assignments.isEmpty() ) {
                Issue issue = makeIssue( Issue.COMPLETENESS, part );
                issue.setDescription( "The task is assigned to no one." );
                issue.setRemediation( "Explicitly assign an individual to the task"
                        + "\nor profile an individual to match the task specifications"
                        + "\nor modify the task specifications so that it matches at least one individual."
                );
                issue.setSeverity( Severity.Minor );
                issues.add( issue );
            }
        }
        return issues;
    }

    /**
     * {@inheritDoc}
     */
    public boolean appliesTo( ModelObject modelObject ) {
        return Part.class.isAssignableFrom( modelObject.getClass() );
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
        return "Unassigned task";
    }

    /**
     * {@inheritDoc}
     */
    public boolean canBeWaived() {
        return true;
    }
}
