package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.analysis.DetectedIssue;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Segment;

import java.util.ArrayList;
import java.util.List;

/**
 * A plan segment addresses no risk.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 12, 2009
 * Time: 4:13:13 PM
 */
public class SegmentWithoutManagedRisk extends AbstractIssueDetector {

    public SegmentWithoutManagedRisk() {
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Segment segment = (Segment) modelObject;
        if ( segment.getRisks().isEmpty() ) {
            DetectedIssue issue = makeIssue( Issue.VALIDITY, segment );
            issue.setSeverity( Issue.Level.Major );
            issue.setDescription( "The plan segment does not address any risk." );
            issue.setRemediation( "Identify one or more risks this plan segment is meant to eliminate or mitigate" );
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
    protected String getLabel() {
        return "No risk addressed by plan segment";
    }
}
