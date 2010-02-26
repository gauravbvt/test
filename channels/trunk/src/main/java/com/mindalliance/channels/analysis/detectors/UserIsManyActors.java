package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.analysis.DetectedIssue;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Severity;
import com.mindalliance.channels.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * A user is personified by more than one actor.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 29, 2009
 * Time: 8:36:21 PM
 */
public class UserIsManyActors extends AbstractIssueDetector {

    public UserIsManyActors() {
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Actor actor = (Actor) modelObject;
        List<Actor> actors = getQueryService().findAllActorsAsUser( actor.getUserName() );
        if ( actors.size() > 1 ) {
            for ( Actor sameUserActor : actors ) {
                if ( !sameUserActor.equals( actor ) ) {
                    User user = getQueryService().getPlanManager().getParticipant( actor.getUserName() );
                    DetectedIssue issue = makeIssue( Issue.VALIDITY, actor );
                    issue.setDescription(
                            "Individual "
                                    + actor.getName()
                                    + " also personifies user "
                                    + user.getNormalizedFullName()
                                    + ".");
                    issue.setRemediation(
                            "Change the user personified by "
                                    + actor.getName()
                                    + "\nor by " + sameUserActor.getName()
                                    + ".");
                    issue.setSeverity( Severity.Major );
                    issues.add( issue );
                }
            }
        }
        return issues;
    }

    /**
     * {@inheritDoc}
     */
    public boolean appliesTo( ModelObject modelObject ) {
        return modelObject instanceof Actor && ( (Actor) modelObject ).getUserName() != null;
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
        return "Another individual is same user";
    }

    /**
     * {@inheritDoc}
     */
    public boolean canBeWaived() {
        return true;
    }
}
