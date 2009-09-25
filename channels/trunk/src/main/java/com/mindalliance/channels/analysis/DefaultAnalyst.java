package com.mindalliance.channels.analysis;

import com.mindalliance.channels.AbstractService;
import com.mindalliance.channels.Analyst;
import com.mindalliance.channels.Detective;
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
import org.springframework.context.Lifecycle;

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
public class DefaultAnalyst extends AbstractService implements Analyst, Lifecycle {

    /**
     * Description separator.
     */
    private static final String DESCRIPTION_SEPARATOR = " -- ";

    /**
     * The query service.
     */
    private QueryService queryService;
    /**
     * The detective service.
     */
    private Detective detective;


    /**
     * Low priority, multi-threaded issues scanner.
     */
    private IssueScanner issueScanner;
    /**
     * Lifecycle status.
     */
    private boolean running;

    public DefaultAnalyst() {
    }

    public QueryService getQueryService() {
        return queryService;
    }

    public void setQueryService( QueryService queryService ) {
        this.queryService = queryService;
    }

    public void setDetective( Detective detective ) {
        this.detective = detective;
    }

    public void setIssueScanner( IssueScanner issueScanner ) {
        this.issueScanner = issueScanner;
    }

    public void start() {
        running = true;
    }

    public void stop() {
        issueScanner.terminate();
    }

    public boolean isRunning() {
        return running;
    }

    public void onAfterCommand( Plan plan ) {
        issueScanner.rescan( plan );
    }

    /**
     * {@inheritDoc}
     */
    public void onStart() {
        issueScanner.scan();
    }

    /**
     * {@inheritDoc}
     */
    public void onStop() {
        stop();
    }


    /**
     * {@inheritDoc}
     */
    public void onDestroy() {
        issueScanner.terminate();
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> listIssues(
            ModelObject modelObject, Boolean includingPropertySpecific, Boolean includingWaived ) {
        if ( includingWaived ) {
            return detectAllIssues( modelObject, null, includingPropertySpecific );
        } else {
            return detectUnwaivedIssues(
                    modelObject,
                    null,
                    includingPropertySpecific );
        }
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> listIssues( ModelObject modelObject, Boolean includingPropertySpecific ) {
        return detectAllIssues( modelObject, null, includingPropertySpecific );
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> listIssues( ModelObject modelObject, String property ) {
        return detectAllIssues( modelObject, property, true );
    }

    public List<Issue> listUnwaivedIssues(
            ModelObject modelObject, Boolean includingPropertySpecific ) {
        return detectUnwaivedIssues(
                modelObject,
                null,
                includingPropertySpecific );
    }

    public List<Issue> listUnwaivedIssues( ModelObject modelObject, String property ) {
        return detectUnwaivedIssues( modelObject, property, true );
    }

    /**
     * {@inheritDoc}
     */
    public Boolean hasIssues( ModelObject modelObject, String property ) {
        return !listIssues( modelObject, property ).isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    public Boolean hasIssues( ModelObject modelObject, Boolean includingPropertySpecific ) {
        return !listIssues( modelObject, includingPropertySpecific ).isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    public Boolean hasUnwaivedIssues( ModelObject modelObject, String property ) {
        return !listUnwaivedIssues( modelObject, property ).isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    public Boolean hasUnwaivedIssues( ModelObject modelObject, Boolean includingPropertySpecific ) {
        return !listUnwaivedIssues( modelObject, includingPropertySpecific ).isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    public String getIssuesSummary( ModelObject modelObject, Boolean includingPropertySpecific ) {
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
            if ( issues.indexOf( issue ) != issues.size() - 1 )
                sb.append( DESCRIPTION_SEPARATOR );
        }
        return sb.toString();
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
    public List<Issue> findAllIssuesFor( ResourceSpec resourceSpec, Boolean specific ) {
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
    public Boolean isValid( ModelObject modelObject ) {
        return test( modelObject, Issue.VALIDITY );
    }

    /**
     * {@inheritDoc}
     */
    public Boolean isComplete( ModelObject modelObject ) {
        return test( modelObject, Issue.COMPLETENESS );
    }

    /**
     * {@inheritDoc}
     */
    public Boolean isRobust( ModelObject modelObject ) {
        return test( modelObject, Issue.ROBUSTNESS );
    }

    private boolean test( ModelObject modelObject, String test ) {
        if ( modelObject instanceof Plan ) {
            return passes( (Plan) modelObject, test );
        } else
            return modelObject instanceof Scenario ? passes( (Scenario) modelObject, test )
                    : hasNoTestedIssue( modelObject, test );
    }

    private boolean passes( Plan plan, String test ) {
        if ( !hasNoTestedIssue( plan, test ) )
            return false;
        for ( Scenario scenario : plan.getScenarios() ) {
            if ( !passes( scenario, test ) )
                return false;
        }
        for ( ModelObject mo : queryService.list( ModelObject.class ) ) {
            if ( mo.isEntity() && !hasNoTestedIssue( mo, test ) )
                return false;
        }
        return true;
    }

    private boolean passes( Scenario scenario, String test ) {
        if ( !hasNoTestedIssue( scenario, test ) )
            return false;
        Iterator<Part> parts = scenario.parts();
        while ( parts.hasNext() ) {
            if ( !hasNoTestedIssue( parts.next(), test ) )
                return false;
        }
        Iterator<Flow> flows = scenario.flows();
        while ( flows.hasNext() ) {
            if ( !hasNoTestedIssue( flows.next(), test ) )
                return false;
        }
        return true;
    }

    private boolean hasNoTestedIssue( ModelObject modelObject, final String test ) {
        return CollectionUtils.select(
                listUnwaivedIssues( modelObject, true ), new Predicate() {
            public boolean evaluate( Object obj ) {
                return ( (Issue) obj ).getType().equals( test );
            }
        } ).isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    public Integer countTestFailures( ModelObject modelObject, String test ) {
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
        for ( ModelObject mo : queryService.list( ModelObject.class ) ) {
            if ( mo.isEntity() )
                count += countTestIssues( mo, test );
        }
        return count;
    }

    private int countFailures( Scenario scenario, String test ) {
        int count = countTestIssues( scenario, test );
        Iterator<Part> parts = scenario.parts();
        while ( parts.hasNext() )
            count += countTestIssues( parts.next(), test );
        Iterator<Flow> flows = scenario.flows();
        while ( flows.hasNext() )
            count += countTestIssues( flows.next(), test );
        return count;
    }

    private int countTestIssues( ModelObject modelObject, final String test ) {
        return CollectionUtils.select(
                listUnwaivedIssues( modelObject, true ), new Predicate() {
            public boolean evaluate( Object object ) {
                return test.equals( ( (Issue) object ).getType() );
            }
        } ).size();
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
        List<Play> plays = queryService.findAllPlays( resourceSpec, specific );
        Set<Part> parts = new HashSet<Part>();
        for ( Play play : plays ) {
            parts.add( play.getPartFor( resourceSpec ) );
            issues.addAll( listIssues( play.getFlow(), true ) );
        }
        for ( Part part : parts ) {
            issues.addAll( listIssues( part, true ) );
        }
        return issues;
    }

    private List<Issue> detectAllIssues(
            ModelObject modelObject,
            String property,
            boolean includingPropertySpecific ) {
        List<Issue> issues = new ArrayList<Issue>();
        if ( property != null ) {
            issues.addAll( detective.detectUnwaivedPropertyIssues( modelObject, property ) );
            issues.addAll( detective.detectWaivedPropertyIssues( modelObject, property ) );
        } else {
            if ( includingPropertySpecific ) {
                issues.addAll( detective.detectUnwaivedIssues( modelObject, true ) );
                issues.addAll( detective.detectWaivedIssues( modelObject, true ) );
            }
            issues.addAll( detective.detectUnwaivedIssues( modelObject, false ) );
            issues.addAll( detective.detectWaivedIssues( modelObject, false ) );
        }
        return issues;
    }

    private List<Issue> detectUnwaivedIssues(
            ModelObject modelObject,
            String property,
            boolean includingPropertySpecific ) {
        if ( property != null ) {
            return detective.detectUnwaivedPropertyIssues( modelObject, property );
        } else {
            List<Issue> issues = new ArrayList<Issue>();
            if ( includingPropertySpecific ) {
                issues.addAll( detective.detectUnwaivedIssues( modelObject, true ) );
            }
            issues.addAll( detective.detectUnwaivedIssues( modelObject, false ) );
            return issues;
        }
    }


}
