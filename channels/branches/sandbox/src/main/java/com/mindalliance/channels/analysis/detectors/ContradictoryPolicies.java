package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.Level;
import com.mindalliance.channels.model.ModelObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Attached policies contradict one another.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 4, 2010
 * Time: 1:25:46 PM
 */
public class ContradictoryPolicies extends AbstractIssueDetector {

    public ContradictoryPolicies() {
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        if ( modelObject.isProhibited() && modelObject.isMandated() ) {
            Issue issue = makeIssue( Issue.VALIDITY, modelObject );
            issue.setDescription( "Prohibiting and mandating policies are attached." );
            issue.setSeverity( Level.Medium );
            issue.setRemediation( "Remove all prohibiting policies"
                    + "\nor remove all mandating policies\n"
                    + "or remove all mandating and prohibiting policies" );
            issues.add( issue );
        }
        return issues;
    }

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
    protected String getLabel() {
        return "Contradictory policies";
    }
}
