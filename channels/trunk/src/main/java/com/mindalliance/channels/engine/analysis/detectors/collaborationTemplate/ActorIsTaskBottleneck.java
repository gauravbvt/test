package com.mindalliance.channels.engine.analysis.detectors.collaborationTemplate;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Assignment;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;

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
public class ActorIsTaskBottleneck extends AbstractIssueDetector {

    public ActorIsTaskBottleneck() {
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> detectIssues( CommunityService communityService, Identifiable modelObject ) {
        QueryService queryService = communityService.getPlanService();
        List<Issue> issues = new ArrayList<Issue>();
        Part part = (Part) modelObject;
        if ( part.isUseful() ) {
            List<Assignment> assignments = queryService.findAllAssignments( part, false );
            if ( assignments.size() == 1 ) {
                Actor actor = assignments.get( 0 ).getActor();
                if ( actor.isSingularParticipation() ) {
                    Issue issue = makeIssue( communityService, Issue.ROBUSTNESS, part );
                    issue.setDescription( actor.getName()
                            + " can only be represented by one participant " +
                            "and is the only known agent assigned to critical task \""
                            + part.getTitle()
                            + "\"." );
                    issue.setRemediation( "Change the specification of the task to allow more agents to be assigned to it"
                            + "\nor add participating agents to the scope who will be assigned to the task"
                            +"\nor allow more than one participant to represent this agent." );
                    issue.setSeverity( computeTaskFailureSeverity( queryService, part ) );
                    issues.add( issue );
                }
            }
        }
        return issues;
    }

    /**
     * {@inheritDoc}
     */
    public boolean appliesTo( Identifiable modelObject ) {
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
        return "Single agent assigned to critical task is bottleneck";
    }

    /**
     * {@inheritDoc}
     */
    public boolean canBeWaived() {
        return true;
    }
}
