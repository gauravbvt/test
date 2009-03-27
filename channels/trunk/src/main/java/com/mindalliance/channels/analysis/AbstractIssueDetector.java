package com.mindalliance.channels.analysis;

import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.Issue;
import com.mindalliance.channels.Service;
import com.mindalliance.channels.pages.Project;

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
     * {@inheritDoc}
     */
    public abstract List<Issue> detectIssues( ModelObject modelObject );

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
     * Get service.
     * @return a service
     */
    protected Service getService() {
        return Project.getProject().getService();
    }

}
