package com.mindalliance.channels.engine.analysis.detectors.collaborationTemplate;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.time.Cycle;
import com.mindalliance.channels.core.model.time.Delay;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;

import java.util.ArrayList;
import java.util.List;

/**
 * Part repeats before it would normally complete.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 5/5/14
 * Time: 10:33 AM
 */
public class PartRepeatsBeforeItCompletes extends AbstractIssueDetector {

    public PartRepeatsBeforeItCompletes() {
    }

    @Override
    public List<? extends Issue> detectIssues( CommunityService communityService, Identifiable identifiable ) {
        List<Issue> issues = new ArrayList<Issue>();
        Part part = (Part) identifiable;
        Cycle taskCycle = part.getCycle();
        Delay timeToComplete = part.getCompletionTime();
        if ( taskCycle != null && !timeToComplete.isImmediate() ) {
            Delay taskRepeatDelay = taskCycle.findSmallestRepeatInterval();
            if ( timeToComplete.compareTo( taskRepeatDelay ) > 0 ) {
                Issue issue = makeIssue( communityService, Issue.VALIDITY, part );
                issue.setDescription( "Task \""
                                + part.getTask()
                                + "\" repeats after " + taskRepeatDelay
                                + " but normally takes "
                                + timeToComplete
                                + " to complete which is too long"
                );
                issue.setRemediation( "Change the repeat cycle of \""
                                + part.getTask()
                                + "\" to be longer that its typical completion time"
                                + "\nor make the task non-repetitive."
                );
                issue.setSeverity( computeTaskFailureSeverity( communityService.getModelService(), part ) );
                issues.add( issue );
            }
        }
        return issues;
    }

    @Override
    public boolean appliesTo( Identifiable identifiable ) {
        return identifiable instanceof Part;
    }

    @Override
    public String getTestedProperty() {
        return null;
    }

    @Override
    protected String getKindLabel() {
        return "Task repeats before it would normally complete";
    }
}
