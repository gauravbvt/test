/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.engine.analysis.detectors;

import com.mindalliance.channels.core.dao.user.ChannelsUserInfo;
import com.mindalliance.channels.core.dao.user.PlanParticipation;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;

import java.util.ArrayList;
import java.util.List;

/**
 * A normal user is not a participant in the plan.
 */
public class NonParticipatingNormalUser extends AbstractIssueDetector {

    public NonParticipatingNormalUser() {
    }

    @Override
    public List<Issue> detectIssues( QueryService queryService, ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Plan plan = queryService.getPlan();
        for ( String username : queryService.getUserDao().getUsernames( plan.getUri() ) ) {
            if ( queryService.findUserRole( username ).equals( ChannelsUserInfo.ROLE_USER ) ) {
                List<PlanParticipation> participations = queryService.findParticipations( username );
                if ( participations.isEmpty() ) {
                    Issue issue = makeIssue( queryService, Issue.COMPLETENESS, plan );
                    issue.setDescription( "Normal user " + username + " does not participate in the plan." );
                    issue.setRemediation( "Assign an agent to user " + username + "\nor unregister user " + username );
                    issue.setSeverity( Level.Low );
                    issues.add( issue );
                }
            }
        }
        return issues;
    }

    @Override
    public boolean appliesTo( ModelObject modelObject ) {
        return modelObject instanceof Plan;
    }

    @Override
    public String getTestedProperty() {
        return null;
    }

    @Override
    protected String getKindLabel() {
        return "Non-planner user is not a participant";
    }

    @Override
    public boolean canBeWaived() {
        return true;
    }
}
