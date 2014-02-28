package com.mindalliance.channels.engine.analysis.detectors.collaborationTemplate;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity has invalid type.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 10, 2009
 * Time: 2:43:16 PM
 */
public class InvalidEntityTyping extends AbstractIssueDetector {

    public InvalidEntityTyping() {
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> detectIssues( CommunityService communityService, Identifiable modelObject ) {
        QueryService queryService = communityService.getModelService();
        List<Issue> issues = new ArrayList<Issue>();
        ModelEntity entity = (ModelEntity) modelObject;
        List<ModelEntity> types = entity.getAllTypes();
        Place locale = queryService.getPlanLocale();

        // Entity is inherently inconsistent with one of its types
        for ( ModelEntity type : types ) {
            if ( !type.validates( entity, locale ) ) {
                Issue issue = makeIssue( communityService, Issue.VALIDITY, entity );
                issue.setDescription( "This " + entity.getKindLabel()
                        + " is categorized as a " + type.getName()
                        + " but is not consistent with its definition." );
                issue.setRemediation( "Do not use  "
                        + entity.getKindLabel()
                        + " " + type.getName()
                        + " as a type of "
                        + entity.getName()
                        + "\n or modify the definition of " + type.getName()
                        + "\n or modify the definition of " + entity.getName() );
                issue.setSeverity( Level.Low );
                issues.add( issue );
            }
        }
        return issues;
    }

    /**
     * {@inheritDoc}
     */
    public boolean appliesTo( Identifiable modelObject ) {
        return ModelEntity.class.isAssignableFrom( modelObject.getClass() )
                && ( (ModelEntity) modelObject ).hasTypes();
    }

    /**
     * {@inheritDoc}
     */
    public String getTestedProperty() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    protected String getKindLabel() {
        return "Conflicting entity categorization";
    }
}
