package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Part;

import java.util.ArrayList;
import java.util.List;

/**
 * A part with a defined task which does not start with the plan segment is never triggered.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 10, 2009
 * Time: 11:24:52 AM
 */
public class NeverTriggeredSpecifiedTask extends AbstractIssueDetector {
    public NeverTriggeredSpecifiedTask() {
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Part part = (Part) modelObject;
        if ( !part.hasDefaultTask() && !part.setIsStartsWithSegment() && !part.isTriggered() ) {
            Issue issue = makeIssue( Issue.COMPLETENESS, part );
            issue.setDescription( "This task, which does not start with the plan segment, is never triggered." );
            issue.setRemediation( "Start this task with the plan segment\n"
                    + "or have an incoming flow trigger it" );
            issue.setSeverity( getQueryService().getPartPriority( part ) );
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

    /**
     * {@inheritDoc}
     */
    protected String getLabel() {
        return "Task never starts";
    }
}
