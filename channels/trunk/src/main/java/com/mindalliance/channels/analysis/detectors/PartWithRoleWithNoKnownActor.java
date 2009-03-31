package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.analysis.DetectedIssue;
import com.mindalliance.channels.Issue;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.Actor;

import java.util.List;
import java.util.ArrayList;

/**
 * Part has a role bu there is no known actor in that role.
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
            List<Actor> actorsInRole = getDqo().findAllActors( part.resourceSpec() );
            if ( actorsInRole.isEmpty() ) {
                Issue issue = new DetectedIssue( Issue.STRUCTURAL, part );
                issue.setDescription( "There is no known actor playing this role." );
                issue.setRemediation( " Identify an actor playing this role." );
                issue.setSeverity( Issue.Level.Major );
                issues.add( issue );
            }
        }
        return issues;
    }

}
