package com.mindalliance.channels.engine.analysis.detectors;

import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Participation;
import com.mindalliance.channels.engine.query.QueryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import java.util.ArrayList;
import java.util.List;

/**
 * No participation for placeholder actor.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/21/11
 * Time: 3:48 PM
 */
public class ActorPlaceHolderWithoutParticipation extends AbstractIssueDetector {

    public ActorPlaceHolderWithoutParticipation() {
    }

    @Override
    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        final Actor actor = (Actor) modelObject;
        if ( actor.isActual() && actor.isPlaceHolder() ) {
            final QueryService queryService = getQueryService();
            boolean hasParticipation = CollectionUtils.exists(
                    queryService.getUserService().getUsernames(),
                    new Predicate() {
                        @Override
                        public boolean evaluate( Object object ) {
                            Participation participation = queryService.findParticipation( (String)object );
                            return participation != null && participation.hasActor( actor );
                        }
                    }
                    );
            if ( !hasParticipation ) {
                Issue issue = makeIssue( Issue.COMPLETENESS, actor );
                issue.setDescription( "Agent \"" + actor.getName() + "\" is a placeholder but with no user assigned.");
                issue.setRemediation( "Assign a user to the agent\nor make the agent not a place holder." );
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
        return "No user participates as placeholder agent";
    }

    @Override
    public boolean canBeWaived() {
        return true;
    }
}
