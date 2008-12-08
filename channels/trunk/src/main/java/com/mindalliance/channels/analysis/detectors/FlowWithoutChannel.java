package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.analysis.Issue;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.Flow;

import java.util.List;
import java.util.ArrayList;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 26, 2008
 * Time: 2:09:12 PM
 */
public class FlowWithoutChannel extends AbstractIssueDetector {

    public FlowWithoutChannel() {
    }

    /**
     * Detect an issue on a model object
     *
     * @param modelObject -- the ModelObject being analyzed
     * @return an Issue or null of none detected
     */
    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = null;
        Flow flow = (Flow) modelObject;
        String channel = flow.getChannel();
        if ( channel == null || channel.trim().isEmpty() ) {
            Issue issue = new Issue( Issue.DEFINITION, modelObject, "channel" );
            issue.setDescription( "The channel is missing." );
            issue.setRemediation( "Provide a channel." );
            issues = new ArrayList<Issue>();
            issues.add(issue);
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
        return "channel";
    }
}
