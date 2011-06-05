package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.Level;
import com.mindalliance.channels.model.ModelEntity;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Place;

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
    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        ModelEntity entity = (ModelEntity) modelObject;
        List<ModelEntity> types = entity.getAllTypes();
        Place locale = getPlan().getLocale();

        // Entity is inherently inconsistent with one of its types
        for ( ModelEntity type : types ) {
            if ( !type.validates( entity, locale ) ) {
                Issue issue = makeIssue( Issue.VALIDITY, entity );
                issue.setDescription( "This " + entity.getKindLabel()
                        + " is types as a " + type.getName()
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
            // Two types used are mutually inconsistent.
            for (ModelEntity otherType : types ) {
                if (!type.equals( otherType )) {
                    if ( !otherType.validates( type, locale ) ) {
                        Issue issue = makeIssue( Issue.VALIDITY, entity );
                         issue.setDescription( "The type " + type.getName()
                                 + " is inconsistent with other type "
                                 + otherType.getName()
                         );
                         issue.setRemediation( "Remove type "
                                 + type.getKindLabel()
                                 + " " + type.getName()
                                 + " from the categorization of  " + entity.getName()
                                 + "\n or remove " + otherType.getName()
                                 + " from the categorization of " + entity.getName()
                                 + "\n or change the definition of " + type.getName()
                                 + "\n or change the definition of " + otherType.getName()
                         );
                         issue.setSeverity( Level.Medium );
                         issues.add( issue );
                    }
                }
            }
        }
        return issues;
    }

    /**
     * {@inheritDoc}
     */
    public boolean appliesTo( ModelObject modelObject ) {
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
