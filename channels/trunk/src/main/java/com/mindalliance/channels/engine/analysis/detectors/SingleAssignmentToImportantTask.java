package com.mindalliance.channels.engine.analysis.detectors;

import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Assignment;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;

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
    public List<Issue> detectIssues( QueryService queryService, ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Part part = (Part) modelObject;
        List<Assignment> assignments = queryService.findAllAssignments( part, false );
        if ( assignments.size() == 1 ) {
            Actor actor = assignments.get( 0 ).getActor();
            if ( actor.isSingularParticipation() ) {
                Level importance = computeTaskFailureSeverity( queryService, part );
                if ( importance.compareTo( Level.Low ) >= 1 ) {
                    Issue issue = makeIssue( queryService, Issue.ROBUSTNESS, part );
                    issue.setDescription( "Task \""
                            + part.getTitle()
                            + "\" is important and yet is assigned to only one agent" +
                            " and this agent can be represented by only one participant." );
                    issue.setSeverity( importance );
                    issue.setRemediation( "Profile agents so that more than one match the task specifications"
                            + "\nor modify the task specifications so that it matches more than one agent"
                            + "\nor allow multiple users to participate as this agent."
                    );
                    issues.add( issue );
                }
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
    protected String getKindLabel() {
        return "Important task assigned to only one single-user agent";
    }

    /**
     * {@inheritDoc}
     */
    public boolean canBeWaived() {
        return true;
    }
}
