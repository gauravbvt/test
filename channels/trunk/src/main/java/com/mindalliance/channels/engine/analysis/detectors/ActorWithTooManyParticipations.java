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
 * More than one user associated to a singular actor.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 6/7/11
 * Time: 9:39 AM
 */
public class ActorWithTooManyParticipations extends AbstractIssueDetector {

    public ActorWithTooManyParticipations() {
    }

    @Override
    public List<Issue> detectIssues( QueryService queryService, ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Actor actor = (Actor) modelObject;
        if ( actor.isSingular() ) {
            List<PlanParticipation> participations = queryService
                    .getPlanParticipationService().getParticipations(
                            queryService.getPlan(),
                            actor,
                            queryService );
            int count = participations.size();
            if ( count > 1 ) {
                Issue issue = makeIssue( queryService, Issue.VALIDITY, actor );
                issue.setDescription( count
                        + " users participate as agent \""
                        + actor.getName() + "\" when there should be at most one." );
                issue.setRemediation( "Unassign " + ( count - 1 ) + " of them" +
                        "\nor make the agent an archetype" +
                        "\nor make the agent a placeholder of which there can be more than one." );
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
        return "Too many participations as agent";
    }
}
