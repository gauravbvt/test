package com.mindalliance.channels.engine.analysis.detectors;

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
 * Task checklist is not confirmed.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/29/13
 * Time: 12:31 PM
 */
public class ChecklistUnconfirmed extends AbstractIssueDetector {

    public ChecklistUnconfirmed() {
    }

    @Override
    public boolean appliesTo( ModelObject modelObject ) {
        return modelObject instanceof Part;
    }

    @Override
    public List<? extends Issue> detectIssues( QueryService queryService, ModelObject modelObject ) {
        Part part = (Part) modelObject;
        Checklist checklist = part.getChecklist();
        List<Issue> issues = new ArrayList<Issue>();
        if ( !checklist.isConfirmed() ) {
            Issue issue = makeIssue( queryService, Issue.COMPLETENESS, part );
            issue.setDescription( "The checklist is not confirmed." );
            issue.setSeverity( computeTaskFailureSeverity( queryService, part ) );
            issue.setRemediation( "Confirm the checklist once deemed satisfactory." );
            issues.add( issue );
        }
        return issues;
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
        return "Checklist is not confirmed";
    }
}