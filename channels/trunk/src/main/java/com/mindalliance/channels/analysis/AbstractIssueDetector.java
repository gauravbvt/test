package com.mindalliance.channels.analysis;

import com.mindalliance.channels.model.ModelObject;

/**
 * Abstract IssueDetector class.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 26, 2008
 * Time: 1:39:47 PM
 */
public abstract class AbstractIssueDetector implements IssueDetector {
    /**
     * Detect an issue on a model object
     *
     * @param modelObject -- the ModelObject being analyzed
     * @return an Issue or null of none detected
     */
    public abstract Issue detectIssue( ModelObject modelObject );

    /**
     * Tests whether the detector applies to the model object
     *
     * @param modelObject -- the ModelObject being analyzed
     * @return whether the detector applies
     */
    public abstract boolean appliesTo( ModelObject modelObject );

    /**
     * Gets the name of the specific property tested, if applicable
     *
     * @return the name of a property or null if test applies to some combination of properties
     */
    public abstract String getTestedProperty();

    /**
     * Tests whether the detector applies to the naed property of the model object
     *
     * @param modelObject -- the ModelObject being analyzed
     * @param property    -- the name of a property of the model object
     * @return whether the detector applies
     */
    public boolean appliesTo( ModelObject modelObject, String property ) {
        return appliesTo( modelObject )
                && property != null
                && property.equals( getTestedProperty() );
    }

}
