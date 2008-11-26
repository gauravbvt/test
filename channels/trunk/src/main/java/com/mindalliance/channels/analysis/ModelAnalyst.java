package com.mindalliance.channels.analysis;

import com.mindalliance.channels.model.ModelObject;

import java.util.List;

/**
 * Analyzes model elements to uncover issues and make recommendations for fixing them.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 26, 2008
 * Time: 9:29:14 AM
 */
public interface ModelAnalyst {

    /**
     * Use all applicable issue detectors to find issues about a model object
     *
     * @param modelObject -- the model object being analyzed
     * @return a list of issues
     */
    List<Issue> findIssues( ModelObject modelObject );

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

    /**
     * Sets the list of issue detectors used for analysis
     *
     * @param issueDetectors -- a list of issue detectors
     */
    void setIssueDetectors( List<IssueDetector> issueDetectors );
}
