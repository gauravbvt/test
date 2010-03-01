package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.analysis.DetectedIssue;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Node;
import com.mindalliance.channels.model.Severity;
import com.mindalliance.channels.util.Matcher;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Detects redundant flows.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 13, 2009
 * Time: 11:23:29 AM
 */
public class RedundantFlow extends AbstractIssueDetector {

    public RedundantFlow() {
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
    public boolean canBeWaived() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    protected String getLabel() {
        return "Redundant flow";
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
        List<Issue> issues = new ArrayList<Issue>();
        Flow flow = (Flow) modelObject;
        Node source = flow.getSource();
        Iterator<Flow> flows = source.sends();
        boolean redundant = false;
        while ( !redundant && flows.hasNext() ) {
            Flow otherFlow = flows.next();
            redundant = ( otherFlow != flow ) && equivalent( flow, otherFlow );
        }
        if ( redundant ) {
            DetectedIssue issue = makeIssue( DetectedIssue.VALIDITY, flow );
            issue.setDescription( "This flow is redundant." );
            issue.setRemediation( "Change the name of information transmitted\nor break up the flow." );
            issue.setSeverity( Severity.Minor );
            issues.add( issue );
        }
        return issues;
    }

    private boolean equivalent( Flow flow, Flow otherFlow ) {
        return flow.getTarget().equals( otherFlow.getTarget() )
                && flow.isAskedFor() == otherFlow.isAskedFor()
                && Matcher.same( flow.getName(), otherFlow.getName() );
    }
}
