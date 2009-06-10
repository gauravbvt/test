package com.mindalliance.channels.analysis;

import com.mindalliance.channels.AbstractService;
import com.mindalliance.channels.Analyst;
import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.model.ResourceSpec;
import com.mindalliance.channels.model.Scenario;
import com.mindalliance.channels.util.Play;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Default implementation of Analyst.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 26, 2008
 * Time: 10:07:27 AM
 */
public class DefaultAnalyst extends AbstractService implements Analyst {

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
    public List<Issue> listIssues(
            ModelObject modelObject,
            boolean includingPropertySpecific,
            boolean includingWaived ) {
        if (includingWaived) {
            return listIssues( modelObject, includingPropertySpecific );
        } else {
            return listUnwaivedIssues( modelObject, includingPropertySpecific );
        }
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
        return !listUnwaivedIssues( modelObject, property ).isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasUnwaivedIssues( ModelObject modelObject, boolean includingPropertySpecific ) {
        return !listUnwaivedIssues( modelObject, includingPropertySpecific ).isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    public String getIssuesSummary( ModelObject modelObject, boolean includingPropertySpecific ) {
        List<Issue> issues = listUnwaivedIssues( modelObject, includingPropertySpecific );
        return summarize( issues );
    }

    /**
     * {@inheritDoc}
     */
    public String getIssuesSummary( ModelObject modelObject, String property ) {
        List<Issue> issues = listUnwaivedIssues( modelObject, property );
        return summarize( issues );
    }

    /**
     * Aggregate the descriptions of issues
     *
     * @param issues -- an iterator on issues
     * @return a string summarizing the issues
     */
    private String summarize( List<Issue> issues ) {
        StringBuilder sb = new StringBuilder();
        for ( Issue issue : issues ) {
            sb.append( issue.getDescription() );
            if ( issues.indexOf( issue ) != issues.size() - 1 ) sb.append( DESCRIPTION_SEPARATOR );
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
        return getChannels().getQueryService();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isValid( ModelObject modelObject ) {
        return test( modelObject, Issue.VALIDITY);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isComplete( ModelObject modelObject ) {
        return test( modelObject, Issue.COMPLETENESS);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isRobust( ModelObject modelObject ) {
        return test( modelObject, Issue.ROBUSTNESS);
    }

    private boolean test( ModelObject modelObject, String test ) {
        if ( modelObject instanceof Plan ) {
            return passes( (Plan) modelObject, test );
        } else if ( modelObject instanceof Scenario ) {
            return passes( (Scenario) modelObject, test );
        } else {
            return hasNoTestedIssue( modelObject, test );
        }
    }

    private boolean passes( Plan plan, String test ) {
        if ( !hasNoTestedIssue( plan, test ) ) return false;
        for ( Scenario scenario : plan.getScenarios() ) {
            if ( !passes( scenario, test ) ) return false;
        }
        for ( ModelObject mo : getQueryService().list(ModelObject.class)) {
            if ( mo.isEntity() && !hasNoTestedIssue( mo, test ) ) return false;
        }
        return true;
    }

    private boolean passes( Scenario scenario, String test ) {
        if ( !hasNoTestedIssue( scenario, test ) ) return false;
        Iterator<Part> parts = scenario.parts();
        while ( parts.hasNext() ) {
            if ( !hasNoTestedIssue( parts.next(), test ) ) return false;
        }
        Iterator<Flow> flows = scenario.flows();
        while ( flows.hasNext() ) {
            if ( !hasNoTestedIssue( flows.next(), test ) ) return false;
        }
        return true;
    }

    private boolean hasNoTestedIssue( ModelObject modelObject, final String test ) {
        return CollectionUtils.select(
                listUnwaivedIssues( modelObject, true ),
                new Predicate() {
                    public boolean evaluate( Object obj ) {
                        return ( (Issue) obj ).getType().equals( test );
                    }
                } ).isEmpty();
    }

    /** {@inheritDoc} */
    public int countTestFailures( ModelObject modelObject, String test ) {
        if ( modelObject instanceof Plan ) {
            return countFailures( (Plan) modelObject, test );
        } else if ( modelObject instanceof Scenario ) {
            return countFailures( (Scenario) modelObject, test );
        } else {
            return countTestIssues( modelObject, test );
        }
    }

     private int countFailures( Plan plan, String test ) {
        int count = countTestIssues( plan, test );
         for ( Scenario scenario : plan.getScenarios() ) {
             count += countFailures( scenario, test );
         }
         for ( ModelObject mo : getQueryService().list(ModelObject.class)) {
             if ( mo.isEntity() ) count += countTestIssues( mo, test );
         }
         return count;
    }

    private int countFailures( Scenario scenario, String test ) {
        int count = countTestIssues( scenario, test );
         Iterator<Part> parts = scenario.parts();
         while ( parts.hasNext() ) {
             count += countTestIssues( parts.next(), test );
         }
         Iterator<Flow> flows = scenario.flows();
         while ( flows.hasNext() ) {
             count += countTestIssues( flows.next(), test );
         }
         return count;
    }

    private int countTestIssues( ModelObject modelObject, final String test ) {
         return CollectionUtils.select(
                 listUnwaivedIssues( modelObject, true ),
                 new Predicate() {
                     public boolean evaluate( Object obj ) {
                         return ((Issue)obj).getType().equals( test );
                     }
                 }).size();
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
        List<Play> plays = getChannels().getQueryService().findAllPlays( resourceSpec, specific );
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
