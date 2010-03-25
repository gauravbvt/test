package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.Level;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Participation;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * A normal user is not a participant in the plan.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 24, 2010
 * Time: 9:07:58 AM
 */
public class NonParticipatingNormalUser extends AbstractIssueDetector {

    public NonParticipatingNormalUser() {
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        QueryService queryService = getQueryService();
        for ( String username : queryService.getUserDetailsService().getAllPlanUsernames() ) {
            if ( queryService.findUserRole( username ).equals( User.ROLE_USER ) ) {
                Participation participation = queryService.findOrCreate(  Participation.class, username );
                if ( participation.getActor() == null ) {
                    Issue issue = makeIssue( Issue.COMPLETENESS, getPlan() );
                    issue.setDescription( "Normal user " + username + " does not participate in the plan." );
                    issue.setRemediation(
                            "Assign an actor to user " + username
                                    + "\nor unregister user " + username
                    );
                    issue.setSeverity( Level.Low );
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
        return modelObject instanceof Plan;
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
        return "Normal user not a participant";
    }
}
