package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.Issue;
import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.Flow;

import java.util.List;
import java.util.ArrayList;

/**
 * Detects issue where a flow's information property is undefined.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 26, 2008
 * Time: 1:35:38 PM
 */
public class UnnamedFlow extends AbstractIssueDetector {

    public UnnamedFlow() {
    }

    /** {@inheritDoc} */
    public List<Issue> doDetectIssues( ModelObject modelObject ) {
        List<Issue> issues = null;
        Flow flow = (Flow) modelObject;
        String name = flow.getName();
        if ( name == null || name.trim().isEmpty() ) {
            Issue issue = new Issue( Issue.DEFINITION, modelObject, "name" );
            issue.setDescription( "The information is missing." );
            issue.setRemediation( "Name the flow." );
            issues = new ArrayList<Issue>();
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
        return "name";
    }
}
