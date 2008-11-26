package com.mindalliance.channels.analysis;

import com.mindalliance.channels.model.ModelObject;

import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 26, 2008
 * Time: 10:07:27 AM
 */
public class DefaultModelAnalyst implements ModelAnalyst {
    /**
     * Use all applicable issue detectors to find issues about a model object
     *
     * @param modelObject -- the model object being analyzed
     * @return a list of issues
     */
    public List<Issue> findIssues( ModelObject modelObject ) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Tests whether a model object has issues
     *
     * @param modelObject -- the model object being analyzed
     * @return whether a model object has issues
     */
    public boolean hasIssues( ModelObject modelObject ) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Tests whether a specifi property of a model object has issues
     *
     * @param modelObject -- the model object being analyzed
     * @param property    -- the specifiec property being analyzed
     * @return whether a specifi property of a model object has issues
     */
    public boolean hasIssues( ModelObject modelObject, String property ) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Produces an aggregate description of issues detected about a model object
     *
     * @param modelObject -- the model object being analyzed
     * @return an aggregate description of issues or an empty string if none
     */
    public String getIssuesSummary( ModelObject modelObject ) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Produces an aggregate description of issues detected about a specific property
     * of a model object
     *
     * @param modelObject -- the model object being analyzed
     * @param property    -- the name of a property
     * @return an aggregate description of issues or an empty string if none
     */
    public String getIssuesSummary( ModelObject modelObject, String property ) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Sets the list of issue detectors used for analysis
     *
     * @param issueDetectors -- a list of issue detectors
     */
    public void setIssueDetectors( List<IssueDetector> issueDetectors ) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
