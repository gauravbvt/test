package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.analysis.DetectedIssue;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.Level;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Node;
import com.mindalliance.channels.model.Part;

import java.util.ArrayList;
import java.util.List;

/**
 * Detects issue where a flow has an undefined source.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 26, 2008
 * Time: 2:01:52 PM
 */
public class FlowWithUndefinedSource extends AbstractIssueDetector {

    public FlowWithUndefinedSource() {
    }

    /** {@inheritDoc} */
    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Flow flow = (Flow) modelObject;
        Node source = flow.getSource();
        if ( source.isPart() && ( (Part) source ).isEmpty() ) {
            DetectedIssue issue = makeIssue( DetectedIssue.VALIDITY, modelObject, "source" );
            issue.setDescription( "The source task is not defined." );
            issue.setRemediation( "Name the agent of the source task\nor name the role\nor name the organization." );
            issue.setSeverity( Level.Medium );
            issues.add( issue );
        }
        return issues;
    }

    /** {@inheritDoc} */
    public boolean appliesTo( ModelObject modelObject ) {
        return modelObject instanceof Flow;
    }

    /** {@inheritDoc} */
     public String getTestedProperty() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    protected String getLabel() {
        return "Source of flow undefined";
    }
}
