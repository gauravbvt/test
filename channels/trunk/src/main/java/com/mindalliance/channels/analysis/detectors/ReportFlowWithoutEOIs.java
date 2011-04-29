package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.Level;
import com.mindalliance.channels.model.ModelObject;

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
public class ReportFlowWithoutEOIs extends AbstractIssueDetector {

    public ReportFlowWithoutEOIs() {
    }


    /**
     * {@inheritDoc}
     */
    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Flow flow = (Flow)modelObject;
        Flow.Intent intent = flow.getIntent();
        if ( intent != null
                && flow.isSharing()
                && intent.equals( Flow.Intent.Report )
                && flow.getEois().isEmpty() ) {
            Issue issue = makeIssue( Issue.COMPLETENESS, flow );
            issue.setDescription( "This report flow shares no element of information.");
            issue.setSeverity( Level.Low );
            issue.setRemediation( "Specify elements of information" +
                    "\nor change the intent of the flow to something other than report.");
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
    protected String getLabel() {
        return "Report flow shares no elements of information";
    }

    @Override
    public boolean canBeWaived() {
        return true;
    }
}
