package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.Level;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Participation;

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
    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>(  );
        Actor actor = (Actor)modelObject;
        if ( actor.isSingular() ) {
            List<Participation> participations = getQueryService().findAllParticipationsFor( actor );
            int count = participations.size();
            if ( count > 1 ) {
                Issue issue = makeIssue( Issue.VALIDITY, actor );
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
