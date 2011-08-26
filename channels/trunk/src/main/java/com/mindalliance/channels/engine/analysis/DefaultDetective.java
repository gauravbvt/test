package com.mindalliance.channels.engine.analysis;

import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.ModelObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Default detective.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Sep 25, 2009
 * Time: 10:28:04 AM
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

    public DefaultDetective() {
    }

    public void setIssueDetectors( List<IssueDetector> issueDetectors ) {
        this.issueDetectors = issueDetectors;
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> detectWaivedIssues(
            ModelObject modelObject,
            Boolean propertySpecific ) {
        return detectIssues(
                getDetectors( modelObject, true, propertySpecific ),
                modelObject );
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> detectUnwaivedIssues(
            ModelObject modelObject,
            Boolean propertySpecific ) {
        return detectIssues(
                getDetectors( modelObject, false, propertySpecific ),
                modelObject );
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> detectWaivedPropertyIssues(
            ModelObject modelObject,
            String property ) {
        return detectIssues(
                getDetectors( modelObject, true, property ),
                modelObject );
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> detectUnwaivedPropertyIssues(
            ModelObject modelObject,
            String property ) {
        return detectIssues(
                getDetectors( modelObject, false, property ),
                modelObject );
    }

    @SuppressWarnings( "unchecked" )
    private List<IssueDetector> getDetectors(
            final ModelObject modelObject,
            final boolean waived,
            final boolean propertySpecific ) {
        return (List<IssueDetector>) CollectionUtils.select(
                issueDetectors,
                new Predicate() {
                    public boolean evaluate( Object obj ) {
                        IssueDetector issueDetector = (IssueDetector) obj;
                        return issueDetector.appliesTo( modelObject )
                                && modelObject.isWaived( issueDetector.getKind() ) == waived
                                && issueDetector.isPropertySpecific() == propertySpecific;
                    }
                } );
    }

    @SuppressWarnings( "unchecked" )
    private List<IssueDetector> getDetectors(
            final ModelObject modelObject,
            final boolean waived,
            final String property ) {
        return (List<IssueDetector>) CollectionUtils.select(
                issueDetectors,
                new Predicate() {
                    public boolean evaluate( Object obj ) {
                        IssueDetector issueDetector = (IssueDetector) obj;
                        return issueDetector.appliesTo( modelObject, property )
                                && modelObject.isWaived( issueDetector.getKind() ) == waived;
                    }
                } );
    }

    private List<Issue> detectIssues(
            List<IssueDetector> issueDetectors,
            ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        for ( IssueDetector detector : issueDetectors ) {
            LOG.debug( "Detecting: " + detector.getKind() );
            List<Issue> detectedIssues = detector.detectIssues( modelObject );
            if ( detectedIssues != null )
                issues.addAll( detectedIssues );
        }
        return issues;
    }


}
