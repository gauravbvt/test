/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.engine.analysis.detectors;

import com.mindalliance.channels.core.Matcher;
import com.mindalliance.channels.core.dao.user.PlanParticipation;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;

import java.util.ArrayList;
import java.util.List;

/**
 * Non-archetypical, non-placeholder actor assigned to a user with a different name.
 */
public class ActorWithNonMatchingParticipation extends AbstractIssueDetector {

    public ActorWithNonMatchingParticipation() {
    }

    @Override
    public List<Issue> detectIssues( QueryService queryService, ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Actor actor = (Actor) modelObject;
        if ( actor.isActual() && actor.isSingularParticipation() && !actor.isAnonymousParticipation() ) {
            List<PlanParticipation> participations = queryService.findParticipations( actor );
            if ( !participations.isEmpty() ) {
                for ( PlanParticipation participation : participations ) {
                    String userFullName = participation.getParticipant().getFullName();
                    if ( !Matcher.same( actor.getName(), userFullName ) ) {
                        Issue issue = makeIssue( queryService, Issue.VALIDITY, actor );
                        issue.setDescription( "Agent \"" + actor.getName() + "\" can only be associated with one visible user "
                                              + "and is assigned to a user with a different name (" + userFullName + ")." );
                        issue.setRemediation(
                                "Assign a user of the same name to the agent" + "\nor assign no user to the agent"
                                + "\nor make the agent anonymous" + "\nor allow more than one user to participate as this agent." );
                        issue.setSeverity( Level.Medium );
                        issues.add( issue );
                    }
                }
            }
        }
        return issues;
    }

    @Override
    public boolean appliesTo( ModelObject modelObject ) {
        return modelObject instanceof Actor;
    }

    @Override
    public String getTestedProperty() {
        return null;
    }

    @Override
    protected String getKindLabel() {
        return "Agent assigned to user with different name";
    }

    @Override
    public boolean canBeWaived() {
        return true;
    }
}
