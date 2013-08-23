package com.mindalliance.channels.engine.analysis.detectors;

import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;
import com.mindalliance.channels.engine.analysis.DetectedIssue;

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
    public List<Issue> detectIssues( QueryService queryService, ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Segment segment = (Segment) modelObject;
        if ( segment.getGoals().isEmpty() ) {
            DetectedIssue issue = makeIssue( queryService, Issue.VALIDITY, segment );
            issue.setSeverity( Level.Medium );
            issue.setDescription( "The segment does not have any goal." );
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
