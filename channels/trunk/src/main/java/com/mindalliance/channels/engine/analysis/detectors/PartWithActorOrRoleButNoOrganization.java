package com.mindalliance.channels.engine.analysis.detectors;

import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;

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
    public List<Issue> detectIssues( QueryService queryService, ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Part part = (Part) modelObject;
        if ( (part.hasActualRole() || part.hasActualActor() )
                && part.getOrganization() == null ) {
            Issue issue = makeIssue( queryService, Issue.VALIDITY, part );
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
    protected String getKindLabel() {
        return "Task's assignable agent or role is named but the organization is unspecified";
    }
}
