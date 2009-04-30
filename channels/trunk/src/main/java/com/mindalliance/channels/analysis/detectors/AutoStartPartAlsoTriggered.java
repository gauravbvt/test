package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Part;

import java.util.List;
import java.util.ArrayList;

/**
 * A part that starts with a scenario is also triggered by a flow.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 10, 2009
 * Time: 11:08:34 AM
 */
public class AutoStartPartAlsoTriggered extends AbstractIssueDetector {

    public AutoStartPartAlsoTriggered() {
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Part part = (Part) modelObject;
        if ( part.isStartsWithScenario() && part.isTriggered() ) {
            Issue issue = makeIssue( Issue.FLOW, part );
            issue.setDescription( "This task is unnecessarily triggered"
                    + " since it starts with the scenario." );
            issue.setRemediation( "Either have no flow trigger this task,"
                    + " or have it not start with the scenario." );
            issue.setSeverity( Issue.Level.Minor );
            issues.add( issue );
        }
        return issues;
    }

    /**
     * {@inheritDoc}
     */
    public boolean appliesTo( ModelObject modelObject ) {
        return modelObject instanceof Part;
    }

    /**
     * {@inheritDoc}
     */
    public String getTestedProperty() {
        return null;
    }
}
