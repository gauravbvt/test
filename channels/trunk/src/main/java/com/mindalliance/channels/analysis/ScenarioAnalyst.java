package com.mindalliance.channels.analysis;

import com.mindalliance.channels.ModelObject;

import java.util.Iterator;

/**
 * Analyzes model elements to uncover issues and make recommendations for fixing them.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 26, 2008
 * Time: 9:29:14 AM
 */
public interface ScenarioAnalyst {

    /**
     * Use all applicable issue detectors to find issues about a model object
     *
     * @param modelObject -- the model object being analyzed
     * @return an iterator on issues detected
     */
    Iterator<Issue> findIssues( ModelObject modelObject );

    /**
     * Use all applicable issue detectors to find issues about a model object's property
     *
     * @param modelObject -- the model object being analyzed
     * @param property    -- the name of a property of the model object
     * @return an iterator on issues detected
     */
    Iterator<Issue> findIssues( ModelObject modelObject, String property );

    /**
     * Tests whether a model object has issues
     *
     * @param modelObject -- the model object being analyzed
     * @return whether a model object has issues
     */
    boolean hasIssues( ModelObject modelObject );

    /**
     * Tests whether a specifi property of a model object has issues
     *
     * @param modelObject -- the model object being analyzed
     * @param property    -- the specifiec property being analyzed
     * @return whether a specifi property of a model object has issues
     */
    boolean hasIssues( ModelObject modelObject, String property );

    /**
     * Produces an aggregate description of issues detected about a model object
     *
     * @param modelObject -- the model object being analyzed
     * @return an aggregate description of issues or an empty string if none
     */
    String getIssuesSummary( ModelObject modelObject );

    /**
     * Produces an aggregate description of issues detected about a specific property
     * of a model object
     *
     * @param modelObject -- the model object being analyzed
     * @param property    -- the name of a property
     * @return an aggregate description of issues or an empty string if none
     */
    String getIssuesSummary( ModelObject modelObject, String property );

}
