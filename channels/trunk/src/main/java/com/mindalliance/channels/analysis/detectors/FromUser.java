package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.Channels;

import java.util.List;

/**
 * Find all issues entered by a user about a model object.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 23, 2009
 * Time: 3:20:52 PM
 */
public class FromUser extends AbstractIssueDetector {

    /**
     * {@inheritDoc}
     */
    public boolean appliesTo( ModelObject modelObject ) {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public String getTestedProperty() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> detectIssues( ModelObject modelObject ) {
        return Channels.queryService().findAllUserIssues( modelObject );
    }
}
