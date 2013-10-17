/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.engine.analysis.detectors;

import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Commitment;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.query.Assignments;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;

import java.util.ArrayList;
import java.util.List;

/**
 * Actor with no assignment and never beneficiary of commitments.
 */
public class UselessActor extends AbstractIssueDetector {

    public UselessActor() {
    }

    @Override
    public List<Issue> detectIssues( QueryService queryService, ModelObject modelObject ) {
        Actor actor = (Actor) modelObject;
        List<Issue> issues = new ArrayList<Issue>();
        Assignments assignments = queryService.getAssignments().with( actor );
        if ( assignments.isEmpty() ) {
            List<Commitment> commitments = queryService.findAllCommitmentsTo(
                    actor,
                    queryService.getAssignments( false ),
                    queryService.findAllFlows() );
            if ( commitments.isEmpty() ) {
                Issue issue = makeIssue( queryService, Issue.COMPLETENESS, actor );
                issue.setDescription( actor.getName()
                        + " is not assigned any task "
                        + "nor is it the beneficiary of any communication commitment" );
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

    @Override
    public boolean appliesTo( ModelObject modelObject ) {
        return Actor.class.isAssignableFrom( modelObject.getClass() )
                && ((Actor)modelObject).isActual();
    }

    @Override
    public String getTestedProperty() {
        return null;
    }

    @Override
    protected String getKindLabel() {
        return "Agent is not assigned any task";
    }

    @Override
    public boolean canBeWaived() {
        return true;
    }
}
