package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.analysis.DetectedIssue;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Severity;

import java.util.ArrayList;
import java.util.List;

/**
 * An information need with an empty description (i.e. no elements of information)
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 14, 2009
 * Time: 2:46:31 PM
 */
public class EmptyNeedOrCapability extends AbstractIssueDetector {

    public EmptyNeedOrCapability() {
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> detectIssues( ModelObject modelObject ) {
        Flow flow = (Flow) modelObject;
        List<Issue> issues = new ArrayList<Issue>();
        if ( !flow.getName().trim().isEmpty() && flow.getEois().isEmpty() ) {
            DetectedIssue issue = makeIssue( Issue.VALIDITY, flow );
            issue.setSeverity( Severity.Minor );
            String needOrCapability = flow.getTarget().isConnector() ? "capability" : "need";
            issue.setDescription( "Information "
                    + needOrCapability
                    + " has no element of information." );
            issue.setRemediation( "Add elements of information" );
            issues.add( issue );
        }
        return issues;
    }

    /**
     * {@inheritDoc}
     */
    public boolean appliesTo( ModelObject modelObject ) {
        return modelObject instanceof Flow
                && ( ( (Flow) modelObject ).getSource().isConnector()
                || ( (Flow) modelObject ).getTarget().isConnector() );
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
        return "Empty information need";
    }

    /**
     * {@inheritDoc}
     */
    public boolean canBeWaived() {
        return true;
    }
}
