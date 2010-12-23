package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.model.Assignment;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.Level;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Part;

import java.util.ArrayList;
import java.util.List;

/**
 * Important task has a single assignment.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 8, 2010
 * Time: 8:20:01 PM
 */
public class SingleAssignmentToImportantTask extends AbstractIssueDetector {
    public SingleAssignmentToImportantTask() {
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Part part = (Part) modelObject;
        List<Assignment> assignments = getQueryService().findAllAssignments( part, false );
        if ( assignments.size() == 1 ) {
            Level importance = this.getTaskFailureSeverity( part );
            if ( importance.compareTo( Level.Low ) >= 1 ) {
                Issue issue = makeIssue( Issue.ROBUSTNESS, part );
                issue.setDescription( "Task \""
                        + part.getTitle()
                        + "\" is important and yet is assigned to only one agent." );
                issue.setSeverity( importance );
                issue.setRemediation( "Profile agents so that more than one match the task specifications"
                        + "\nor modify the task specifications so that it matches more than one agent."
                );
                issues.add( issue );
            }
        }
        return issues;
    }

    /**
     * {@inheritDoc}
     */
    public boolean appliesTo( ModelObject modelObject ) {
        return modelObject instanceof Part;
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
        return "Important task has only one assignment";
    }

    /**
     * {@inheritDoc}
     */
    public boolean canBeWaived() {
        return true;
    }
}
