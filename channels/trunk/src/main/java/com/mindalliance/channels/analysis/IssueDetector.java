package com.mindalliance.channels.analysis;

import com.mindalliance.channels.model.ModelObject;

/**
 * A strategy for detecting issues on a model object
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 26, 2008
 * Time: 9:44:36 AM
 */
public interface IssueDetector {

    /**
     * Detect an issue on a model object
     *
     * @param modelObject -- the ModelObject being analyzed
     * @return an Issue or null of none detected
     */
    Issue detectIssue( ModelObject modelObject );

    /**
     * Tests whether the detector applies to the model object as a whole
     *
     * @param modelObject -- the ModelObject being analyzed
     * @return whether the detector applies
     */
    boolean appliesTo( ModelObject modelObject );

    /**
     * Gets the name of the specific property tested, if applicable
     * @return the name of a property or null if test applies to some combination of properties
     */
    String getTestedProperty();
}
