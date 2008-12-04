package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.analysis.Issue;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.Part;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Dec 2, 2008
 * Time: 12:47:49 PM
 */
public class PartWithoutRole extends AbstractIssueDetector {

    /**
     * Detect an issue on a model object
     *
     * @param modelObject -- the ModelObject being analyzed
     * @return an Issue or null of none detected
     */
    public Issue detectIssue( ModelObject modelObject ) {
        Issue issue = null;
        Part part = (Part) modelObject;
        if ( part.getRole() == null) {
            issue = new Issue( Issue.DEFINITION, modelObject, getTestedProperty() );
            issue.setDescription( "The role is missing." );
            issue.setRemediation( "Name a role." );
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
        return modelObject instanceof Part;
    }

    /**
     * Gets the name of the specific property tested, if applicable
     *
     * @return the name of a property or null if test applies to some combination of properties
     */
    public String getTestedProperty() {
        return "role";
    }
}
