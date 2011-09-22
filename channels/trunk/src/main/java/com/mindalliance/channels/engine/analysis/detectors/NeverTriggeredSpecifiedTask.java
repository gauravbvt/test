/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.engine.analysis.detectors;

import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;

import java.util.ArrayList;
import java.util.List;

/**
 * A part with a defined task which does not start with the plan segment is never triggered.
 */
public class NeverTriggeredSpecifiedTask extends AbstractIssueDetector {

    public NeverTriggeredSpecifiedTask() {
    }

    @Override
    public List<Issue> detectIssues( QueryService queryService, ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Part part = (Part) modelObject;
        if ( !part.hasDefaultTask() && !part.isAutoStarted() && !part.isTriggered() ) {
            Issue issue = makeIssue( queryService, Issue.COMPLETENESS, part );
            issue.setDescription(
                "This task may never start; it does not start with the plan segment, is not ongoing, and it is never triggered." );
            issue.setRemediation(
                "Start this task with the plan segment" + "\nor make it ongoing"
                + "\nor have an incoming sharing flow trigger it" );
            issue.setSeverity( queryService.computePartPriority( part ) );
            issues.add( issue );
        }
        return issues;
    }

    @Override
    public boolean appliesTo( ModelObject modelObject ) {
        return modelObject instanceof Part;
    }

    @Override
    public String getTestedProperty() {
        return null;
    }

    @Override
    protected String getKindLabel() {
        return "Task never starts";
    }
}
