package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.Level;
import com.mindalliance.channels.model.ModelObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Dec 21, 2010
 * Time: 12:03:56 PM
 */
public class ImportantFlowNotOperational  extends AbstractIssueDetector {

    public ImportantFlowNotOperational() {
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Flow flow = (Flow) modelObject;
        if ( flow.isOperationalizable() && !flow.isOperational() ) {
            Level importance = getSharingFailureSeverity( flow );
            if ( importance.compareTo( Level.Low ) >= 1 ) {
                Issue issue = makeIssue( Issue.ROBUSTNESS, flow );
                issue.setDescription( "Flow \""
                        + flow.getTitle()
                        + "\" is important but is not operational." );
                issue.setSeverity( importance );
                issue.setRemediation( "Make the flow operational." );
                issues.add( issue );
            }
        }
        return issues;
    }

    /**
     * {@inheritDoc}
     */
    public boolean appliesTo( ModelObject modelObject ) {
        return modelObject instanceof Flow;
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
    protected String getKindLabel() {
        return "Important sharing flow is not operational";
    }

    /**
     * {@inheritDoc}
     */
    public boolean canBeWaived() {
        return true;
    }
}

