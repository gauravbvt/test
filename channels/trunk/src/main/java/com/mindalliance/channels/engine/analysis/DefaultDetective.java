/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.engine.analysis;

import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.query.QueryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Default detective.
 */
public class DefaultDetective implements Detective {

    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( DefaultDetective.class );

    /**
     * Issue detectors registered with the detective.
     */
    private List<IssueDetector> issueDetectors = new ArrayList<IssueDetector>();

    //-------------------------------
    public DefaultDetective() {
    }

    //-------------------------------
    @Override
    public List<Issue> detectUnwaivedIssues( QueryService queryService, ModelObject modelObject,
                                             Boolean propertySpecific ) {
        return detectIssues( queryService, getDetectors( modelObject, false, propertySpecific ), modelObject );
    }

    @Override
    public List<Issue> detectUnwaivedPropertyIssues( QueryService queryService, ModelObject modelObject,
                                                     String property ) {
        return detectIssues( queryService, getDetectors( modelObject, false, property ), modelObject );
    }

    @Override
    public List<Issue> detectWaivedIssues( QueryService queryService, ModelObject modelObject,
                                           Boolean propertySpecific ) {
        return detectIssues( queryService, getDetectors( modelObject, true, propertySpecific ), modelObject );
    }

    private static List<Issue> detectIssues( QueryService queryService, List<IssueDetector> detectors,
                                             ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        for ( IssueDetector detector : detectors ) {
            LOG.debug( "Detecting: " + detector.getKind() );
            List<Issue> detectedIssues = detector.detectIssues( queryService, modelObject );
            if ( detectedIssues != null )
                issues.addAll( detectedIssues );
        }
        return issues;
    }

    @SuppressWarnings( "unchecked" )
    private List<IssueDetector> getDetectors( final ModelObject modelObject, final boolean waived,
                                              final boolean propertySpecific ) {
        return (List<IssueDetector>) CollectionUtils.select( issueDetectors, new Predicate() {
            @Override
            public boolean evaluate( Object object ) {
                IssueDetector issueDetector = (IssueDetector) object;
                return issueDetector.isApplicable( modelObject, waived, propertySpecific );
            }
        } );
    }

    @Override
    public List<Issue> detectWaivedPropertyIssues( QueryService queryService, ModelObject modelObject,
                                                   String property ) {
        return detectIssues( queryService, getDetectors( modelObject, true, property ), modelObject );
    }

    @SuppressWarnings( "unchecked" )
    private List<IssueDetector> getDetectors( final ModelObject modelObject, final boolean waived,
                                              final String property ) {
        return (List<IssueDetector>) CollectionUtils.select( issueDetectors, new Predicate() {
            @Override
            public boolean evaluate( Object object ) {
                IssueDetector issueDetector = (IssueDetector) object;
                return issueDetector.appliesTo( modelObject, property )
                       && modelObject.isWaived( issueDetector.getKind() ) == waived;
            }
        } );
    }

    //-------------------------------
    /**
     * Configurable issue detectors.
     * @param issueDetectors a list of issue detectors
     */
    public void setIssueDetectors( List<IssueDetector> issueDetectors ) {
        this.issueDetectors = Collections.unmodifiableList( issueDetectors );
    }
}
