package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.analysis.DetectedIssue;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.Level;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Segment;

import java.util.ArrayList;
import java.util.List;

/**
 * A plan segment without goal.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 12, 2009
 * Time: 4:13:13 PM
 */
public class SegmentWithoutGoal extends AbstractIssueDetector {

    public SegmentWithoutGoal() {
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Segment segment = (Segment) modelObject;
        if ( segment.getGoals().isEmpty() ) {
            DetectedIssue issue = makeIssue( Issue.VALIDITY, segment );
            issue.setSeverity( Level.Medium );
            issue.setDescription( "The plan segment does not have any goal." );
            issue.setRemediation( "Add a goal to the segment"
                    + "\nor remove the segment." );
            issues.add( issue );
        }
        return issues;
    }

    /**
     * {@inheritDoc}
     */
    public boolean appliesTo( ModelObject modelObject ) {
        return modelObject instanceof Segment;
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
    protected String getKindLabel() {
        return "Segment has no stated goal";
    }

    /**
     * {@inheritDoc}
     */
    public boolean canBeWaived() {
        return true;
    }
}
