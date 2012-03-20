/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.engine.analysis.detectors;

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
 * No participation for placeholder actor.
 */
public class ActorWithoutParticipation extends AbstractIssueDetector {

    public ActorWithoutParticipation() {
    }

    @Override
    public List<Issue> detectIssues( final QueryService queryService, ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        final Actor actor = (Actor) modelObject;
        if ( actor.isActual() ) {
            List<PlanParticipation> participations = queryService.findParticipations( actor );
            boolean hasParticipation = !participations.isEmpty();
            if ( !hasParticipation ) {
                Issue issue = makeIssue( queryService, Issue.COMPLETENESS, actor );
                issue.setDescription( "Agent \"" + actor.getName() + "\" has no user assigned." );
                issue.setRemediation( "Assign a user to the agent." );
                issue.setSeverity( Level.Medium );
                issues.add( issue );
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
        return "No user participates as agent";
    }

    @Override
    public boolean canBeWaived() {
        return true;
    }
}
