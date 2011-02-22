package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.Level;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Prohibitable;

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
        if ( ((Prohibitable)modelObject).isProhibited() && modelObject.hasMandatingPolicy() ) {
            Issue issue = makeIssue( Issue.VALIDITY, modelObject );
            issue.setDescription( "Mandating policy attached to prohibited "
                    + modelObject.getKindLabel()
                    + "." );
            issue.setSeverity( Level.Medium );
            issue.setRemediation( "Remove prohibition"
                    + "\nor remove all mandating policies" );
            issues.add( issue );
        }
        return issues;
    }

    /**
     * {@inheritDoc}
     */
    public boolean appliesTo( ModelObject modelObject ) {
        return modelObject instanceof Prohibitable;
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
