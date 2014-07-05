package com.mindalliance.channels.engine.analysis.detectors.collaborationTemplate;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.Specable;
import com.mindalliance.channels.core.query.Assignments;
import com.mindalliance.channels.core.query.Commitments;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 10/17/13
 * Time: 9:18 AM
 */
public class EntityNotInvolvedAgainstExpectation extends AbstractIssueDetector {

    public EntityNotInvolvedAgainstExpectation() {
    }

    @Override
    public boolean appliesTo( Identifiable identifiable ) {
        return identifiable instanceof ModelEntity;
    }

    @Override
    public List<? extends Issue> detectIssues( CommunityService communityService, Identifiable modelObject ) {
        QueryService queryService = communityService.getModelService();
        List<Issue> issues = new ArrayList<Issue>(  );
        ModelEntity entity = (ModelEntity)modelObject;
        if ( queryService.isInvolvementExpected( entity ) ) {
            Assignments assignments = queryService.getAssignments();
            Commitments commitments = queryService.getAllCommitments( assignments );
            if ( !queryService.isInvolved( entity, assignments, commitments ) ) {
                Issue issue = makeIssue( communityService, Issue.COMPLETENESS, entity );
                issue.setDescription( "The " + entity.getTypeName() + " \"" + entity.getName() + "\" is expected to be involved but is not." );
                issue.setSeverity( Level.Medium );
                issue.setRemediation( (entity instanceof Specable
                        ? "Involve it in a task"
                        : "Involve it in a flow")
                        + "\nor remove the involvement expectation."
                );
                issues.add( issue );
            }
        }
        return issues;
    }

    @Override
    public String getTestedProperty() {
        return null;
    }

    @Override
    protected String getKindLabel() {
        return "Entity not involved while expected to be";
    }
}
