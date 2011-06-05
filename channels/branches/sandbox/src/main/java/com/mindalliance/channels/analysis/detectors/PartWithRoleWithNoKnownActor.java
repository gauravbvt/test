package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.Level;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Part;

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
        return "No known agent in task's named role";
    }

    /**
     * {@inheritDoc}
     */
    public boolean appliesTo( ModelObject modelObject ) {
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
    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Part part = (Part) modelObject;
        if ( part.getRole() != null && part.getOrganization() != null && part.getActor() == null ) {
            List<Actor> actorsInRole = getQueryService().findAllActualActors( part.resourceSpec() );
            if ( part.getOrganization().isActorsRequired() && actorsInRole.isEmpty() ) {
                Issue issue = makeIssue( Issue.COMPLETENESS, part );
                issue.setDescription( "There is no known agent playing this role." );
                issue.setRemediation( "Name an agent assigned to the task" +
                        "\nor add a participating agent to the plan's scope with this role." );
                issue.setSeverity( Level.Medium );
                issues.add( issue );
            }
        }
        return issues;
    }

}
