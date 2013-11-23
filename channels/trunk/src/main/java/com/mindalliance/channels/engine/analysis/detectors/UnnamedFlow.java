package com.mindalliance.channels.engine.analysis.detectors;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;
import com.mindalliance.channels.engine.analysis.DetectedIssue;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.query.QueryService;

import java.util.ArrayList;
import java.util.List;

/**
 * Detects issue where a flow's information property is undefined.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 26, 2008
 * Time: 1:35:38 PM
 */
public class UnnamedFlow extends AbstractIssueDetector {

    public UnnamedFlow() {
    }

    /** {@inheritDoc} */
    public List<Issue> detectIssues( CommunityService communityService, Identifiable modelObject ) {
        List<Issue> issues = null;
        Flow flow = (Flow) modelObject;
        String name = flow.getName();
        if ( name == null || name.trim().isEmpty() ) {
            DetectedIssue issue = makeIssue( communityService, DetectedIssue.VALIDITY, modelObject, "name" );
            issue.setDescription( "The name of the information is missing." );
            issue.setRemediation( "Name the information flow." );
            issues = new ArrayList<Issue>();
            issue.setSeverity( Level.High );
            issues.add( issue );
        }
        return issues;
    }

    /** {@inheritDoc} */
    public boolean appliesTo( Identifiable modelObject ) {
        return modelObject instanceof Flow;
    }

    /** {@inheritDoc} */
    public String getTestedProperty() {
        return "name";
    }

    protected String getKindLabel() {
        return "Information flow is not named";
    }
}
