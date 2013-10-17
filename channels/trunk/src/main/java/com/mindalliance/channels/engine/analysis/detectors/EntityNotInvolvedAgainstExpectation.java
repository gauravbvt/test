package com.mindalliance.channels.engine.analysis.detectors;

import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Specable;
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
    public boolean appliesTo( ModelObject modelObject ) {
        return modelObject.isEntity();
    }

    @Override
    public List<? extends Issue> detectIssues( QueryService queryService, ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>(  );
        ModelEntity entity = (ModelEntity)modelObject;
        if ( queryService.isInvolvementExpected( entity ) ) {
            if ( !queryService.isInvolved( entity, queryService.getAssignments(), queryService.getAllCommitments() ) ) {
                Issue issue = makeIssue( queryService, Issue.COMPLETENESS, entity );
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
