package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.Level;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Part;

import java.util.ArrayList;
import java.util.List;

/**
 * A part specifies a role but no organization.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 9, 2009
 * Time: 1:12:37 PM
 */
public class PartWithActorOrRoleButNoOrganization extends AbstractIssueDetector {

    public PartWithActorOrRoleButNoOrganization() {
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
    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Part part = (Part) modelObject;
        if ( (part.hasActualRole() || part.hasActualActor() )
                && part.getOrganization() == null ) {
            Issue issue = makeIssue( Issue.VALIDITY, part );
            issue.setDescription( "The task names "
                    + (part.hasActualRole() ? "a role" : "")
                    + (part.hasActualRole() && part.hasActualActor() ? " and " : "")
                    + (part.hasActualActor() ? "an agent" : "")
                    + " but does not specify an organization." );
            issue.setRemediation( "Specify the organization for this task." );
            issue.setSeverity( Level.Low );
            issues.add( issue );
        }
        return issues;
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
        return "organization";
    }

    /**
     * {@inheritDoc}
     */
    protected String getLabel() {
        return "Task's agent or role is named but not the organization";
    }
}
