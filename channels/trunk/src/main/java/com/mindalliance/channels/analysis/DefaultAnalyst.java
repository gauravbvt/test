package com.mindalliance.channels.analysis;

import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.ResourceSpec;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.Channels;
import com.mindalliance.channels.Analyst;
import com.mindalliance.channels.util.Play;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.CollectionUtils;

/**
 * Default implementation of Analyst.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 26, 2008
 * Time: 10:07:27 AM
 */
public class DefaultAnalyst implements Analyst {

    private static final String DESCRIPTION_SEPARATOR = " -- ";

    /**
     * Issue detectors registered with the scenario analyst.
     */
    private List<IssueDetector> issueDetectors = new ArrayList<IssueDetector>();

    public DefaultAnalyst() {
    }

    /**
     * {@inheritDoc}
     */
    public Iterator<Issue> findIssues( ModelObject modelObject, boolean includingPropertySpecific ) {
        return new IssueIterator( issueDetectors, modelObject, includingPropertySpecific );
    }

    /**
     * {@inheritDoc}
     */
    public Iterator<Issue> findIssues( ModelObject modelObject, String property ) {
        return new IssueIterator( issueDetectors, modelObject, property );
    }

    @SuppressWarnings( "unchecked" )
    private Iterator<Issue> findUnwaivedIssues(
            final ModelObject modelObject,
            boolean includingPropertySpecific ) {
        List<IssueDetector> unwaivedDetectors = (List<IssueDetector>) CollectionUtils.select(
                issueDetectors,
                new Predicate() {
                    public boolean evaluate( Object obj ) {
                        IssueDetector issueDetector = (IssueDetector) obj;
                        return !modelObject.isWaived( issueDetector.getKind() );
                    }
                } );
        return new IssueIterator( unwaivedDetectors, modelObject, includingPropertySpecific );
    }

    @SuppressWarnings( "unchecked" )
    private Iterator<Issue> findUnwaivedIssues( final ModelObject modelObject, String property ) {
        List<IssueDetector> unwaivedDetectors = (List<IssueDetector>) CollectionUtils.select(
                issueDetectors,
                new Predicate() {
                    public boolean evaluate( Object obj ) {
                        IssueDetector issueDetector = (IssueDetector) obj;
                        return !modelObject.isWaived( issueDetector.getKind() );
                    }
                } );
        return new IssueIterator( unwaivedDetectors, modelObject, property );
    }


    /**
     * {@inheritDoc}
     */
    public List<Issue> listIssues( ModelObject modelObject, boolean includingPropertySpecific ) {
        List<Issue> issues = new ArrayList<Issue>();
        Iterator<Issue> iterator = findIssues( modelObject, includingPropertySpecific );
        while ( iterator.hasNext() ) issues.add( iterator.next() );
        return issues;
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> listIssues( ModelObject modelObject, String property ) {
        List<Issue> issues = new ArrayList<Issue>();
        Iterator<Issue> iterator = findIssues( modelObject, property );
        while ( iterator.hasNext() ) issues.add( iterator.next() );
        return issues;
    }

    public List<Issue> listUnwaivedIssues( ModelObject modelObject, boolean includingPropertySpecific ) {
        List<Issue> issues = new ArrayList<Issue>();
        Iterator<Issue> iterator = findUnwaivedIssues( modelObject, includingPropertySpecific );
        while ( iterator.hasNext() ) issues.add( iterator.next() );
        return issues;
    }

    public List<Issue> listUnwaivedIssues( ModelObject modelObject, String property ) {
        List<Issue> issues = new ArrayList<Issue>();
        Iterator<Issue> iterator = findUnwaivedIssues( modelObject, property );
        while ( iterator.hasNext() ) issues.add( iterator.next() );
        return issues;
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasIssues( ModelObject modelObject, String property ) {
        return findIssues( modelObject, property ).hasNext();
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasIssues( ModelObject modelObject, boolean includingPropertySpecific ) {
        return findIssues( modelObject, includingPropertySpecific ).hasNext();
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasUnwaivedIssues( ModelObject modelObject, String property ) {
        return findUnwaivedIssues( modelObject, property ).hasNext();
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasUnwaivedIssues( ModelObject modelObject, boolean includingPropertySpecific ) {
        return findUnwaivedIssues( modelObject, includingPropertySpecific ).hasNext();
    }

    /**
     * {@inheritDoc}
     */
    public String getIssuesSummary( ModelObject modelObject, boolean includingPropertySpecific ) {
        Iterator<Issue> issues = findUnwaivedIssues( modelObject, includingPropertySpecific );
        return summarize( issues );
    }

    /**
     * {@inheritDoc}
     */
    public String getIssuesSummary( ModelObject modelObject, String property ) {
        Iterator<Issue> issues = findUnwaivedIssues( modelObject, property );
        return summarize( issues );
    }

    /**
     * Aggregate the descriptions of issues
     *
     * @param issues -- an iterator on issues
     * @return a string summarizing the issues
     */
    private String summarize( Iterator<Issue> issues ) {
        StringBuilder sb = new StringBuilder();
        while ( issues.hasNext() ) {
            Issue issue = issues.next();
            sb.append( issue.getDescription() );
            if ( issues.hasNext() ) sb.append( DESCRIPTION_SEPARATOR );
        }
        return sb.toString();
    }

    /**
     * Sets the list of issue detectors used for analysis
     *
     * @param issueDetectors -- a list of issue detectors
     */
    public void setIssueDetectors( List<IssueDetector> issueDetectors ) {
        this.issueDetectors = issueDetectors;
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> findAllIssuesFor( ResourceSpec resourceSpec ) {
        return findAllIssuesFor( resourceSpec, false );
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> findAllIssuesFor( ResourceSpec resourceSpec, boolean specific ) {
        List<Issue> issues = new ArrayList<Issue>();
        if ( !resourceSpec.isAnyActor() ) {
            issues.addAll( listIssues( resourceSpec.getActor(), true ) );
        }
        if ( !resourceSpec.isAnyOrganization() ) {
            issues.addAll( listIssues( resourceSpec.getOrganization(), true ) );
        }
        if ( !resourceSpec.isAnyRole() ) {
            issues.addAll( listIssues( resourceSpec.getRole(), true ) );
        }
        if ( !resourceSpec.isAnyJurisdiction() ) {
            issues.addAll( listIssues( resourceSpec.getJurisdiction(), true ) );
        }
        issues.addAll( findAllIssuesInPlays( resourceSpec, specific ) );
        return issues;
    }

    /**
     * {@inheritDoc}
     */
    public QueryService getQueryService() {
        return Channels.instance().getQueryService();
    }

    /**
     * Find the issues on parts and flows for all plays of a resource
     *
     * @param resourceSpec a resource
     * @param specific     whether the match is "equals" or "narrows or equals"
     * @return a list of issues
     */
    private List<Issue> findAllIssuesInPlays( ResourceSpec resourceSpec, boolean specific ) {
        List<Issue> issues = new ArrayList<Issue>();
        List<Play> plays = Channels.queryService().findAllPlays( resourceSpec, specific );
        Set<Part> parts = new HashSet<Part>();
        for ( Play play : plays ) {
            parts.add( play.getPartFor( resourceSpec ) );
            Iterator<Issue> iterator = findIssues( play.getFlow(), true );
            while ( iterator.hasNext() ) issues.add( iterator.next() );
        }
        for ( Part part : parts ) {
            Iterator<Issue> iterator = findIssues( part, true );
            while ( iterator.hasNext() ) issues.add( iterator.next() );
        }
        return issues;
    }


}
