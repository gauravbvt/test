package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.analysis.Issue;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.Flow;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.Node;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 26, 2008
 * Time: 2:01:52 PM
 */
public class FlowWithUndefinedSource extends AbstractIssueDetector {

    public FlowWithUndefinedSource() {
    }

    /**
     * Detect an issue on a model object
     *
     * @param modelObject -- the ModelObject being analyzed
     * @return an Issue or null of none detected
     */
    public Issue detectIssue( ModelObject modelObject ) {
        Issue issue = null;
        Flow flow = (Flow) modelObject;
        Node source = flow.getSource();
        if ( source.isPart() && ( (Part) source ).isUndefined() ) {
            issue = new Issue( Issue.DEFINITION, modelObject, "source" );
            issue.setDescription( "The source is not defined." );
            issue.setRemediation( "Name the actor, role or organization of the source." );
        }
        return issue;
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
        return "source";
    }
}
