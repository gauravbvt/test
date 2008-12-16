package com.mindalliance.channels.analysis;

import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.util.SimpleCache;

import java.util.List;
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
     * Cache of issues found by the detector.
     */
    private SimpleCache<ModelObject,List<Issue>> cache =
            new SimpleCache<ModelObject,List<Issue>>();

    /**
     * {@inheritDoc}
     */
    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = cache.get( modelObject, modelObject.lastModified() );
        if ( issues == null ) {
            issues = doDetectIssues( modelObject );
            cache.put( modelObject, issues  );
        }
        return issues;
    }

    /**
     * {@inheritDoc}
     */
    public abstract boolean appliesTo( ModelObject modelObject );

    /**
     * {@inheritDoc}
     */
    public abstract String getTestedProperty();

    /**
     * {@inheritDoc}
     */
    public boolean appliesTo( ModelObject modelObject, String property ) {
        return appliesTo( modelObject )
                && property != null
                && property.equals( getTestedProperty() );
    }

    /**
     * Do the work of detecting issues about the model object.
     *
     * @param modelObject -- the model object being analyzed
     * @return -- a list of issues
     */
    protected abstract List<Issue> doDetectIssues( ModelObject modelObject );

}
