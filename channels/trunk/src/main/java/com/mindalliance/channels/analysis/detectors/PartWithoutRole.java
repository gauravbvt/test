package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.analysis.DetectedIssue;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.Level;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Part;

import java.util.ArrayList;
import java.util.List;

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

    /**
     * {@inheritDoc}
     */
    protected String getLabel() {
        return "Task without named role";
    }

    /** {@inheritDoc} */
    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Part part = (Part) modelObject;
        if ( part.getActor() != null && part.getOrganization() != null && part.getRole() == null  ) {
            DetectedIssue issue = makeIssue( DetectedIssue.VALIDITY, modelObject, getTestedProperty() );
            issue.setDescription( "The role for the task is missing." );
            issue.setRemediation( "Name a role for the task." );
            issue.setSeverity( Level.Low );
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
