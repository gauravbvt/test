package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.analysis.DetectedIssue;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.Issue;

import java.util.List;
import java.util.ArrayList;

/**
 * Detects the issue where a part has no given role but has actor and organization.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Dec 2, 2008
 * Time: 12:47:49 PM
 */
public class PartWithoutRole extends AbstractIssueDetector {

    /**
     * {@inheritDoc}
     */
    public boolean canBeWaived() {
        return true;
    }

    /** {@inheritDoc} */
    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Part part = (Part) modelObject;
        if ( part.getActor() != null && part.getOrganization() != null && part.getRole() == null  ) {
            DetectedIssue issue = makeIssue( DetectedIssue.DEFINITION, modelObject, getTestedProperty() );
            issue.setDescription( "The role is missing." );
            issue.setRemediation( "Name a role." );
            issue.setSeverity( Issue.Level.Minor );
            issues.add( issue );
        }
        return issues;
    }

    /** {@inheritDoc} */
    public boolean appliesTo( ModelObject modelObject ) {
        return modelObject instanceof Part;
    }

    /** {@inheritDoc} */
    public String getTestedProperty() {
        return "role";
    }
}
