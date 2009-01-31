package com.mindalliance.channels.analysis;

import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.ResourceSpec;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.Issue;
import com.mindalliance.channels.util.Play;
import com.mindalliance.channels.pages.Project;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

/**
 * Default implementation of Analyst
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 26, 2008
 * Time: 10:07:27 AM
 */
// TODO - cache detected issues, reset cache according to changes to the model
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


    /**
     * {@inheritDoc}
     */
    public List<Issue> listIssues( ModelObject modelObject, boolean includingPropertySpecific ) {
        List<Issue> issues = new ArrayList<Issue>();
        Iterator<Issue> iterator = findIssues( modelObject, includingPropertySpecific );
        while (iterator.hasNext()) issues.add( iterator.next() );
        return issues;
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> listIssues( ModelObject modelObject, String property ) {
        List<Issue> issues = new ArrayList<Issue>();
        Iterator<Issue> iterator = findIssues( modelObject, property );
        while (iterator.hasNext()) issues.add( iterator.next() );
        return issues;
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
    public boolean hasIssues( ModelObject modelObject, String property ) {
        return findIssues( modelObject, property ).hasNext();
    }

    /**
     * {@inheritDoc}
     */
    public String getIssuesSummary( ModelObject modelObject, boolean includingPropertySpecific ) {
        Iterator<Issue> issues = findIssues( modelObject, includingPropertySpecific );
        return summarize( issues );
    }

    /**
     * {@inheritDoc}
     */
    public String getIssuesSummary( ModelObject modelObject, String property ) {
        Iterator<Issue> issues = findIssues( modelObject, property );
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
    public List<Issue> findAllIssuesFor( ResourceSpec resource ) {
        List<Issue> issues = new ArrayList<Issue>();
        if ( !resource.isAnyActor() ) {
            issues.addAll( listIssues( resource.getActor(), true ) );
        }
        if ( !resource.isAnyOrganization() ) {
            issues.addAll( listIssues( resource.getOrganization(), true ) );
        }
        if ( !resource.isAnyRole() ) {
            issues.addAll( listIssues( resource.getRole(), true ) );
        }
        if ( !resource.isAnyJurisdiction() ) {
            issues.addAll( listIssues( resource.getJurisdiction(), true ) );
        }
        issues.addAll( findAllIssuesInPlays( resource ) );
        return issues;
    }

    /**
     * Find the issues on parts and flows for all plays of a resource
     *
     * @param resource a resource
     * @return a list of issues
     */
    private List<Issue> findAllIssuesInPlays( ResourceSpec resource ) {
        List<Issue> issues = new ArrayList<Issue>();
        List<Play> plays = Project.service().findAllPlays( resource );
        Set<Part> parts = new HashSet<Part>();
        for ( Play play : plays ) {
            parts.add( play.getPart() );
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
