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
 * Part is ongoing and starts with segment.
 */
public class PartIsOngoingAndStartsWithSegment extends AbstractIssueDetector {

    @Override
    public List<Issue> detectIssues( QueryService queryService, ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>(  );
        Part part = (Part)modelObject;
        if ( part.isOngoing() && part.isStartsWithSegment() )  {
            Issue issue = makeIssue( queryService, Issue.VALIDITY, part );
            issue.setDescription( "A task can't be both ongoing and start with the segment." );
            issue.setRemediation( "Make the task ongoing but not starting with the segment" +
                    "\nor make the task start with the segment but not ongoing" +
                    "\nor make the task neither ongoing nor starting with the segment." );
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
        return "Task is ongoing and yet also starts with the segment";
    }
}
