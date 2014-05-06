package com.mindalliance.channels.engine.analysis.detectors.collaborationTemplate;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.time.Cycle;
import com.mindalliance.channels.core.model.time.Delay;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;
import com.sun.xml.ws.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * The repeat cycle of a flow in a repeated task is longer than the repeat cycle of the task.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 5/5/14
 * Time: 9:34 AM
 */
public class FlowCycleExceedsPartCycle extends AbstractIssueDetector {

    public FlowCycleExceedsPartCycle() {
    }

    @Override
    public List<? extends Issue> detectIssues( CommunityService communityService, Identifiable identifiable ) {
        List<Issue> issues = new ArrayList<Issue>(  );
        Flow sharing = (Flow)identifiable;
        Cycle flowCycle = sharing.getCycle();
        Part part = sharing.getInitiatingPart();
        Cycle taskCycle = part.getCycle();
        if ( flowCycle != null && taskCycle != null ) {
            Delay flowRepeatDelay = flowCycle.findSmallestRepeatInterval();
            Delay taskRepeatDelay = taskCycle.findSmallestRepeatInterval();
            if ( flowRepeatDelay.compareTo( taskRepeatDelay ) >= 0 ) {
                Issue issue = makeIssue( communityService, Issue.VALIDITY, sharing );
                issue.setDescription( StringUtils.capitalize( sharing.getDescriptiveLabel() )
                        + " is initiated by task \""
                        + part.getTask()
                        + "\" and repeats after " + flowRepeatDelay
                        + " which is not quickly enough "
                        + " since the task is itself repeated after " + taskRepeatDelay );
                issue.setRemediation( "Make the repeat cycle of "
                        + sharing.getDescriptiveLabel()
                        + " fall within the repeat cycle of task \""
                        + part.getTask()
                        + "\" "
                        + "\n make either non-repetitive."
                );
                issue.setSeverity( computeSharingFailureSeverity( communityService.getModelService(), sharing ) );
                issues.add( issue );
            }
        }
        return issues;
    }

    @Override
    public boolean appliesTo( Identifiable identifiable ) {
        return identifiable instanceof Flow && ((Flow)identifiable).isSharing();
    }

    @Override
    public String getTestedProperty() {
        return null;
    }

    @Override
    protected String getKindLabel() {
        return "Repeat cycle of flow longer than repeat cycle of task initiating it";
    }
}
