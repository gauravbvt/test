package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.Level;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Part;

import java.util.ArrayList;
import java.util.List;

/**
 * A part that starts with a plan segment is also triggered by a flow.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 10, 2009
 * Time: 11:08:34 AM
 */
public class AutoStartPartAlsoTriggered extends AbstractIssueDetector {

    public AutoStartPartAlsoTriggered() {
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Part part = (Part) modelObject;
        if ( part.isStartsWithSegment() && part.isTriggered() ) {
            Issue issue = makeIssue( Issue.COMPLETENESS, part );
            issue.setDescription( "This task is unnecessarily triggered"
                    + " since it starts with the plan segment." );
            issue.setRemediation( "Have no flow trigger this task\n"
                    + "or have the task not start with the plan segment." );
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
        return null;
    }

    /**
     * {@inheritDoc}
     */
    protected String getLabel() {
        return "Needlessly triggered task";
    }

    /**
     * {@inheritDoc}
     */
    public boolean canBeWaived() {
        return true;
    }
}
