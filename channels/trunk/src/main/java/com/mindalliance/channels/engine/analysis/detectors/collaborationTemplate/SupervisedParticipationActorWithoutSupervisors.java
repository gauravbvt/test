package com.mindalliance.channels.engine.analysis.detectors.collaborationTemplate;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;

import java.util.ArrayList;
import java.util.List;

/**
 * A supervised participation actor has no supervisor.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 10/25/12
 * Time: 12:23 PM
 */
public class SupervisedParticipationActorWithoutSupervisors extends AbstractIssueDetector {

    public SupervisedParticipationActorWithoutSupervisors() {
    }

    @Override
    public boolean appliesTo( Identifiable modelObject ) {
        return modelObject instanceof Actor;
    }

    @Override
    public List<? extends Issue> detectIssues( CommunityService communityService, Identifiable modelObject ) {
        QueryService queryService = communityService.getModelService();
        List<Issue> issues = new ArrayList<Issue>(  );
        Actor actor = (Actor)modelObject;
        if ( actor.isSupervisedParticipation() ) {
            if ( queryService.findAllSupervisorsOf( actor ).isEmpty() ) {
                Issue issue = makeIssue( communityService, Issue.COMPLETENESS, actor );
                issue.setDescription( "Participation as agent \"" + actor.getName()
                        + "\" must be confirmed by supervisors and " +
                        "the agent has none." );
                issue.setRemediation( "Do not require participation confirmation" +
                        "\nor add a job in an organization for the agent that has a supervisor." );
                issue.setSeverity( Level.Medium );
                issues.add( issue );
            }
        }
        return issues;
    }

    @Override
    public String getTestedProperty() {
        return null;
    }

    @Override
    protected String getKindLabel() {
        return "Agent with supervised participation has no supervisor";
    }
}
