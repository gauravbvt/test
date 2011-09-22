/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.engine.analysis.detectors;

import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.query.QueryService;

import java.util.List;

/**
 * Find all issues entered by a user about a model object.
 */
public class FromUser extends AbstractIssueDetector {

    @Override
    public boolean appliesTo( ModelObject modelObject ) {
        return true;
    }

    @Override
    public String getTestedProperty() {
        return null;
    }

    @Override
    protected String getKindLabel() {
        return "User defined";
    }

    @Override
    public List<Issue> detectIssues( QueryService queryService, ModelObject modelObject ) {
        return queryService.findAllUserIssues( modelObject );
    }
}
