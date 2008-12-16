package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.analysis.Issue;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.Flow;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.Node;

import java.util.List;
import java.util.ArrayList;

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
    public List<Issue> doDetectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Flow flow = (Flow) modelObject;
        Node source = flow.getSource();
        if ( source.isPart() && ( (Part) source ).isUndefined() ) {
            Issue issue = new Issue( Issue.DEFINITION, modelObject, "source" );
            issue.setDescription( "The source is not defined." );
            issue.setRemediation( "Name the actor, role or organization of the source." );
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
        return "source";
    }
}
