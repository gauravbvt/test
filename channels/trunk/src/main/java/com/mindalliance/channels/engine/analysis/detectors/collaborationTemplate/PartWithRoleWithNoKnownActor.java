package com.mindalliance.channels.engine.analysis.detectors.collaborationTemplate;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;

import java.util.ArrayList;
import java.util.List;

/**
 * Part has a role but there is no known actor in that role.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 30, 2009
 * Time: 12:22:16 PM
 */
public class PartWithRoleWithNoKnownActor extends AbstractIssueDetector {

    public PartWithRoleWithNoKnownActor() {
    }

    /**
     * {@inheritDoc}
     */
    public boolean canBeWaived() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    protected String getKindLabel() {
        return "No known agent has task's assignable role";
    }

    /**
     * {@inheritDoc}
     */
    public boolean appliesTo( Identifiable modelObject ) {
        return modelObject instanceof Part;
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
    public List<Issue> detectIssues( CommunityService communityService, Identifiable modelObject ) {
        QueryService queryService = communityService.getModelService();
        List<Issue> issues = new ArrayList<Issue>();
        Part part = (Part) modelObject;
        if ( part.getRole() != null && part.getOrganization() != null && part.getActor() == null ) {
            List<Actor> actorsInRole = queryService.findAllActualActors( part.resourceSpec() );
            if ( part.getOrganization().isActorsRequired() && actorsInRole.isEmpty() ) {
                Issue issue = makeIssue( communityService, Issue.COMPLETENESS, part );
                issue.setDescription( "There is no known agent playing this role." );
                issue.setRemediation( "Name an agent assigned to the task" +
                        "\nor add an agent to an organization employed in this role." );
                issue.setSeverity( Level.Medium );
                issues.add( issue );
            }
        }
        return issues;
    }

}
