package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Assignment;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Part;

import java.util.ArrayList;
import java.util.List;

/**
 * A critical (useful) task is assignable to only one known agent.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Oct 1, 2010
 * Time: 2:53:29 PM
 */
public class AgentIsTaskBottleneck extends AbstractIssueDetector {

    public AgentIsTaskBottleneck() {
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Part part = (Part) modelObject;
        if ( part.isUseful() ) {
            List<Assignment> assignments = getQueryService().findAllAssignments( part, false );
            if ( assignments.size() == 1 ) {
                Actor actor = assignments.get( 0 ).getActor();
                if ( !actor.isArchetype() ) {
                    Issue issue = makeIssue( Issue.ROBUSTNESS, part );
                    issue.setDescription( actor.getName()
                            + " is the only known agent assigned to critical task \""
                            + part.getTitle()
                            + "\"." );
                    issue.setRemediation( "Change the specification of the task to allow more agents to be assigned to it"
                            + "\nor add participating agents to the scope who will be assigned to the task." );
                    issue.setSeverity( getTaskFailureSeverity( part ) );
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
    protected String getLabel() {
        return "Single agent assigned to critical task is bottleneck";
    }

    /**
     * {@inheritDoc}
     */
    public boolean canBeWaived() {
        return true;
    }
}
