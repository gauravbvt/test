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

    /**
     * Detect an issue on a model object
     *
     * @param modelObject -- the ModelObject being analyzed
     * @return a list of Issues or null of none detected
     */
    public List<Issue> detectIssues( ModelObject modelObject ) {
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

    /**
     * Tests whether the detector applies to the model object
     *
     * @param modelObject -- the ModelObject being analyzed
     * @return whether the detector applies
     */
    public boolean appliesTo( ModelObject modelObject ) {
        return modelObject instanceof Flow;
    }

    /**
     * Gets the name of the specific property tested, if applicable
     *
     * @return the name of a property or null if test applies to some combination of properties
     */
    public String getTestedProperty() {
        return "name";
    }
}
