package com.mindalliance.channels.engine.analysis.detectors;

import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;
import com.mindalliance.channels.engine.query.QueryService;

import java.util.ArrayList;
import java.util.List;

/**
 * Part is ongoing and starts with segment.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 9/22/11
 * Time: 4:16 PM
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
        return "Task is both ongoing and starting with the segment";
    }
}
