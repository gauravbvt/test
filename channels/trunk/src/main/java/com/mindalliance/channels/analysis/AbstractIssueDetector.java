package com.mindalliance.channels.analysis;

import com.mindalliance.channels.ModelObject;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.Date;

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
     * A timestamped list of issues.
     */
    static class FoundIssues {
        /**
         * Date when issues were found
         */
        private Date timestamp;
        /**
         * Issues found
         */
        private List<Issue> issues;

        FoundIssues( List<Issue> issues ) {
            this.issues = issues;
            timestamp = new Date();
        }

        public Date getTimestamp() {
            return timestamp;
        }

        public List<Issue> getIssues() {
            return issues;
        }

    }

    /**
     * Cache of issues found by the detector.
     */
    private Map<ModelObject, FoundIssues> cache =
            Collections.synchronizedMap( new HashMap<ModelObject, FoundIssues>() );

    /**
     * {@inheritDoc}
     */
    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = getCachedIssues( modelObject );
        if ( issues == null ) {
            issues = doDetectIssues( modelObject );
            cacheIssues( modelObject, issues );
        }
        return issues;
    }

    private void cacheIssues( ModelObject modelObject, List<Issue> issues ) {
        cache.put( modelObject, new FoundIssues( issues ) );
    }

    private List<Issue> getCachedIssues( ModelObject modelObject ) {
        FoundIssues foundIssues = cache.get( modelObject );
        if ( foundIssues != null && !foundIssues.getTimestamp()
                .before( modelObject.lastModified() ) )
        {
            return foundIssues.getIssues();
        } else {
            return null;
        }
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
