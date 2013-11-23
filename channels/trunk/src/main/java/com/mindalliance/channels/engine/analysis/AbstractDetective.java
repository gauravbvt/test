package com.mindalliance.channels.engine.analysis;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.ModelObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 11/22/13
 * Time: 4:49 PM
 */
public abstract class AbstractDetective implements Detective {

    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( AbstractDetective.class );

    /**
     * Issue detectors registered with the detective.
     */
    private List<IssueDetector> issueDetectors = new ArrayList<IssueDetector>();

    //-------------------------------
    public AbstractDetective() {
    }

    //-------------------------------
    @Override
    public List<Issue> detectUnwaivedIssues( CommunityService communityService, Identifiable identifiable,
                                             Boolean propertySpecific ) {
        return detectIssues( communityService, getDetectors( identifiable, false, propertySpecific, communityService ), identifiable );
    }

    @Override
    public List<Issue> detectUnwaivedPropertyIssues( CommunityService communityService, Identifiable identifiable,
                                                     String property ) {
        return detectIssues( communityService, getDetectors( identifiable, false, property, communityService ), identifiable );
    }

    @Override
    public List<? extends Issue> detectWaivedIssues( CommunityService communityService, Identifiable identifiable,
                                                     Boolean propertySpecific ) {
        return detectIssues( communityService, getDetectors( identifiable, true, propertySpecific, communityService ), identifiable );
    }

    private static List<Issue> detectIssues( CommunityService communityService, List<IssueDetector> detectors,
                                             Identifiable identifiable ) {
        List<Issue> issues = new ArrayList<Issue>();
        for ( IssueDetector detector : detectors ) {
            LOG.debug( "Detecting: " + detector.getKind() );
            List<? extends Issue> detectedIssues = detector.detectIssues( communityService, identifiable );
            if ( detectedIssues != null )
                issues.addAll( detectedIssues );
        }
        return issues;
    }

    @SuppressWarnings( "unchecked" )
    private List<IssueDetector> getDetectors( final Identifiable identifiable,
                                              final boolean waived,
                                              final boolean propertySpecific,
                                              final CommunityService communityService ) {
        return (List<IssueDetector>) CollectionUtils.select( issueDetectors, new Predicate() {
            @Override
            public boolean evaluate( Object object ) {
                IssueDetector issueDetector = (IssueDetector) object;
                return issueDetector.isApplicable( identifiable, waived, propertySpecific );
            }
        } );
    }

    @Override
    public List<Issue> detectWaivedPropertyIssues( CommunityService communityService,
                                                   Identifiable identifiable,
                                                   String property ) {
        return detectIssues( communityService, getDetectors( identifiable, true, property, communityService ), identifiable );
    }

    @SuppressWarnings( "unchecked" )
    private List<IssueDetector> getDetectors( final Identifiable identifiable,
                                              final boolean waived,
                                              final String property,
                                              CommunityService communityService ) {
        return (List<IssueDetector>) CollectionUtils.select( issueDetectors, new Predicate() {
            @Override
            public boolean evaluate( Object object ) {
                IssueDetector issueDetector = (IssueDetector) object;
                return issueDetector.appliesTo( identifiable, property )
                        && AbstractIssueDetector.isWaived( identifiable, issueDetector.getKind() ) == waived;
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
