package com.mindalliance.channels.engine.analysis.detectors;

import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;

import java.util.ArrayList;
import java.util.List;

/**
 * A report flow has no element of information.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Sep 14, 2010
 * Time: 2:25:12 PM
 */
public class EmptySharingFlow extends AbstractIssueDetector {

    public EmptySharingFlow() {
    }


    /**
     * {@inheritDoc}
     */
    public List<Issue> detectIssues( QueryService queryService, ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Flow flow = (Flow)modelObject;
        if ( flow.isSharing() && flow.getEffectiveEois().isEmpty() ) {
            Issue issue = makeIssue( queryService, Issue.COMPLETENESS, flow );
            issue.setDescription( "This sharing flow has no element of information.");
            issue.setSeverity( Level.Medium );
            issue.setRemediation( "Specify elements of information." );
            issues.add( issue );
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
        return "Report flow shares no elements of information";
    }

    @Override
    public boolean canBeWaived() {
        return true;
    }
}
