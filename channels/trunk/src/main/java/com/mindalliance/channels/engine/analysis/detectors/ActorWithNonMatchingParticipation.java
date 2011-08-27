package com.mindalliance.channels.engine.analysis.detectors;

import com.mindalliance.channels.core.Matcher;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Participation;
import com.mindalliance.channels.engine.query.QueryService;

import java.util.ArrayList;
import java.util.List;

/**
 * Non-archetypical, non-placeholder actor assigned to a user with a different name.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/21/11
 * Time: 4:13 PM
 */
public class ActorWithNonMatchingParticipation extends AbstractIssueDetector {

    public ActorWithNonMatchingParticipation() {
    }

    @Override
    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        final Actor actor = (Actor) modelObject;
        if ( actor.isActual() && !actor.isPlaceHolder() && !actor.isArchetype() ) {
            final QueryService queryService = getQueryService();
            Participation participation = null;
            for ( String userName : queryService.getUserService().getUsernames() ) {
                Participation p = queryService.findParticipation( userName );
                if ( p != null && p.hasActor( actor ) ) {
                    participation = p;
                    break;
                }
            }
            if ( participation != null ) {
                String userFullName = queryService.getUserFullName( participation );
                if ( !Matcher.same( actor.getName(), userFullName ) ) {
                    Issue issue = makeIssue( Issue.VALIDITY, actor );
                    issue.setDescription( "Agent \"" + actor.getName() + "\" is not a placeholder nor an archetype "
                            + "and is assigned to a user with a different name ("
                            + userFullName
                            + ")." );
                    issue.setRemediation( "Assign a user of the same name to the agent" +
                            "\nor assign no user to the agent" +
                            "\nor make the agent a place holder" +
                            "\nor make the agent an archetype." );
                    issue.setSeverity( Level.Medium );
                    issues.add( issue );
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
