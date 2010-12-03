package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Commitment;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.Level;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.query.Assignments;

import java.util.ArrayList;
import java.util.List;

/**
 * Actor with no assignment and never beneficiary of commitments.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 10, 2009
 * Time: 11:58:27 AM
 */
public class UselessActor extends AbstractIssueDetector {

    public UselessActor() {
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> detectIssues( ModelObject modelObject ) {
        Actor actor = (Actor) modelObject;
        List<Issue> issues = new ArrayList<Issue>();
        Assignments assignments = getQueryService().getAssignments().with( actor );
        if ( assignments.isEmpty() ) {
            List<Commitment> commitments = getQueryService().findAllCommitmentsTo( actor );
            if ( commitments.isEmpty() ) {
                Issue issue = makeIssue( Issue.COMPLETENESS, actor );
                issue.setDescription( actor.getName()
                        + " is not assigned any task "
                        + "nor is it the beneficiary of any sharing commitment" );
                issue.setRemediation( "Explicitly assign agent " + actor.getName() + " to a task"
                        + "\nor define a task that matches the agent's profile"
                        + "\nor modify the agent's profile so that it matches one or more task specifications"
                );
                issue.setSeverity( Level.Low );
                issues.add( issue );
            }
        }
        return issues;
    }

    /**
     * {@inheritDoc}
     */
    public boolean appliesTo( ModelObject modelObject ) {
        return Actor.class.isAssignableFrom( modelObject.getClass() )
                && ((Actor)modelObject).isActual();
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
        return "Agent is not assigned any task";
    }

    /**
     * {@inheritDoc}
     */
    public boolean canBeWaived() {
        return true;
    }
}
