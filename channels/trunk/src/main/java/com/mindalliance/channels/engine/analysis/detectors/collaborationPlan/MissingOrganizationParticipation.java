package com.mindalliance.channels.engine.analysis.detectors.collaborationPlan;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;

import java.util.ArrayList;
import java.util.List;

/**
 * No organization participating as placeholder.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 1/8/13
 * Time: 1:51 PM
 */
public class MissingOrganizationParticipation extends AbstractIssueDetector {

    public MissingOrganizationParticipation() {
    }

    @Override
    public boolean appliesTo( Identifiable identifiable ) {
        return identifiable instanceof Organization && ((Organization)identifiable).isPlaceHolder();
    }

    @Override
    public String getKindLabel() {
        return "No organization participates as placeholder";
    }

    @Override
    public List<Issue> detectIssues( CommunityService communityService, Identifiable identifiable ) {
        List<Issue> issues = new ArrayList<Issue>(  );
        Organization placeholder = (Organization)identifiable;
        int count = communityService.getParticipationManager()
                .findAgenciesParticipatingAs( placeholder, communityService ).size();
        if ( count == 0 ) {
            Issue issue = makeIssue( communityService, Issue.COMPLETENESS, placeholder );
            issue.setDescription( "No organization participates as placeholder " + placeholder.getName() );
            issue.setRemediation( "Register an organization as " + placeholder.getName() );
            issue.setSeverity( Level.High );
            issues.add(  issue );
        }
        return issues;
    }

    @Override
    public String getTestedProperty() {
        return null;
    }
}
