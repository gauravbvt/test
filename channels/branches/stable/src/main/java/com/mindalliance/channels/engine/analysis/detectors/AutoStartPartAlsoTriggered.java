/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.engine.analysis.detectors;

import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;

import java.util.ArrayList;
import java.util.List;

/**
 * A part that starts with a plan segment is also triggered by a flow.
 */
public class AutoStartPartAlsoTriggered extends AbstractIssueDetector {

    public AutoStartPartAlsoTriggered() {
    }

    @Override
    public List<Issue> detectIssues( QueryService queryService, ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Part part = (Part) modelObject;
        if ( part.isAutoStarted() && part.isTriggered() ) {
            Issue issue = makeIssue( queryService, Issue.COMPLETENESS, part );
            issue.setDescription( "This task is unnecessarily triggered"
                    + " since it "
                    + ( part.isStartsWithSegment() ? " starts with the plan segment." : " is ongoing." ) );
            issue.setRemediation( "Have no flow trigger this task\n"
                    + "or have the task not "
                    + ( part.isStartsWithSegment() ? " start with the plan segment." : " be ongoing." )
                    + " start with the plan segment." );
            issue.setSeverity( Level.Low );
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
        return "Needlessly triggered task";
    }

    @Override
    public boolean canBeWaived() {
        return true;
    }
}
