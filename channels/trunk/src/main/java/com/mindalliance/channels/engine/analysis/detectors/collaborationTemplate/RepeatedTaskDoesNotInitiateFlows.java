package com.mindalliance.channels.engine.analysis.detectors.collaborationTemplate;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 5/5/14
 * Time: 12:10 PM
 */
public class RepeatedTaskDoesNotInitiateFlows extends AbstractIssueDetector {

    public RepeatedTaskDoesNotInitiateFlows() {
    }

    @Override
    public List<? extends Issue> detectIssues( CommunityService communityService, Identifiable identifiable ) {
        List<Issue> issues = new ArrayList<Issue>();
        Part part = (Part) identifiable;
        if ( part.isRepeating() ) {
            if ( part.getAllInitiatedSharingFlows().isEmpty() ) {
                Issue issue = makeIssue( communityService, Issue.COMPLETENESS, part );
                issue.setDescription(
                        "Task \""
                        + part.getTask()
                        + "\" repeats but initiates no communications"
                );
                issue.setRemediation(
                        "Have task \""
                                + part.getTask()
                                + "\" initiate one or more communications"
                                + "\nor make the task non-repetitive."
                );
                issue.setSeverity( Level.Medium );
                issues.add( issue );
            }
        }
        return issues;

    }

    @Override
    public boolean canBeWaived() {
        return true;
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
        return "Repeated task does not initiate flows";
    }
}
