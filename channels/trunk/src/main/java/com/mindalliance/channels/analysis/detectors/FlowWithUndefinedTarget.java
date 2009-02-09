package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.analysis.DetectedIssue;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.Flow;
import com.mindalliance.channels.Node;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.Issue;

import java.util.List;
import java.util.ArrayList;

/**
 * Detects issue where a flow has an undefined target.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 26, 2008
 * Time: 2:07:28 PM
 */
public class FlowWithUndefinedTarget extends AbstractIssueDetector {

    public FlowWithUndefinedTarget() {
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> doDetectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Flow flow = (Flow) modelObject;
        Node target = flow.getTarget();
        if ( target.isPart() && ( (Part) target ).isUndefined() ) {
            DetectedIssue issue = new DetectedIssue( DetectedIssue.DEFINITION, modelObject, "target" );
            issue.setDescription( "The target is not defined." );
            issue.setRemediation( "Name the actor, role or organization of the target." );
            issue.setSeverity( Issue.Level.Major );
            issues.add( issue );
        }
        return issues;
    }

    /**
     * {@inheritDoc}
     */
    public boolean appliesTo( ModelObject modelObject ) {
        return modelObject instanceof Flow;
    }

    /**
     * {@inheritDoc}
     */
    public String getTestedProperty() {
        return "target";
    }
}
