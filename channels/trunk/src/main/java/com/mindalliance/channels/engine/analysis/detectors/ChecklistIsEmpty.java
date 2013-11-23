package com.mindalliance.channels.engine.analysis.detectors;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.checklist.Checklist;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Checklist is empty - it only has unconstrained, implied steps.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/29/13
 * Time: 10:42 AM
 */
public class ChecklistIsEmpty extends AbstractIssueDetector {

    public ChecklistIsEmpty() {
    }

    @Override
    public boolean appliesTo( Identifiable modelObject ) {
        return modelObject instanceof Part;
    }

    @Override
    public List<? extends Issue> detectIssues( CommunityService communityService, Identifiable modelObject ) {
        QueryService queryService = communityService.getPlanService();
        Part part = (Part) modelObject;
        Checklist checklist = part.getEffectiveChecklist();
        List<Issue> issues = new ArrayList<Issue>();
        if ( checklist.isEmpty() ) {
            Issue issue = makeIssue( communityService, Issue.COMPLETENESS, part );
            issue.setDescription( "The checklist is empty; it contains no step." );
            issue.setSeverity( computeTaskFailureSeverity( queryService, part ) );
            issue.setRemediation( "Add action steps" +
                    "\nor add sent notifications or requests to the task" +
                    "\nor have the task be triggered by a request (one step will be to answer the request)." );
            issues.add( issue );
        }
        return issues;
    }

    @Override
    public boolean canBeWaived() {
        return true;
    }

    @Override
    public String getTestedProperty() {
        return null;
    }

    @Override
    protected List<String> getTags() {
        return Arrays.asList( "checklist" );
    }

    @Override
    protected String getKindLabel() {
        return "Task has an empty checklist";
    }
}
