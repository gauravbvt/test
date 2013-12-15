package com.mindalliance.channels.engine.analysis;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Assignment;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.model.Requirement;
import com.mindalliance.channels.core.model.ResourceSpec;
import com.mindalliance.channels.core.model.Role;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.query.Play;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Abstract doctor implementation.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/3/13
 * Time: 2:27 PM
 */
public class AbstractDoctor implements Doctor {

    /**
     * Description separator.
     */
    private static final String DESCRIPTION_SEPARATOR = " -- ";


    /**
     * Low priority, multi-threaded issues scanner.
     */
    private IssueScanner issueScanner;

    /**
     * The collaboration template detective.
     */
    private Detective detective;


    public AbstractDoctor() {
    }

    /**
     * Set the detective to use for templates.
     *
     * @param detective an issue detector manager
     */
    public void setDetective( Detective detective ) {
        this.detective = detective;
    }


    public Detective getDetective( ) {
        return detective;
    }

    public IssueScanner getIssueScanner() {
        return issueScanner;
    }


    @Override
    public void setIssueScanner( IssueScanner issueScanner ) {
        this.issueScanner = issueScanner;
    }

    @Override
    public List<? extends Issue> listIssues( CommunityService communityService, Identifiable identifiable,
                                             Boolean includingPropertySpecific, Boolean includingWaived ) {
        return includingWaived ?
                detectAllIssues( communityService, identifiable, null, includingPropertySpecific ) :
                detectUnwaivedIssues( communityService, identifiable, null, includingPropertySpecific );
    }

    @Override
    public List<Issue> listIssues( CommunityService communityService, Identifiable identifiable,
                                   Boolean includingPropertySpecific ) {
        return detectAllIssues( communityService, identifiable, null, includingPropertySpecific );
    }

    @Override
    public List<Issue> listIssues( CommunityService communityService, Identifiable identifiable, String property ) {
        return detectAllIssues( communityService, identifiable, property, true );
    }

    @Override
    public List<? extends Issue> listUnwaivedIssues( CommunityService communityService, Identifiable identifiable,
                                                     Boolean includingPropertySpecific ) {
        return detectUnwaivedIssues( communityService, identifiable, null, includingPropertySpecific );
    }

    @Override
    public List<? extends Issue> listUnwaivedIssues( CommunityService communityService, Identifiable identifiable, String property ) {
        return detectUnwaivedIssues( communityService, identifiable, property, true );
    }

    @Override
    public List<? extends Issue> listUnwaivedIssues( CommunityService communityService, Assignment assignment,
                                                     Boolean includingPropertySpecific ) {
        return detectUnwaivedIssues( communityService, assignment, includingPropertySpecific );
    }

    @Override
    public List<? extends Issue> listWaivedIssues( CommunityService communityService, Identifiable identifiable,
                                                   Boolean includingPropertySpecific ) {
        return detectWaivedIssues( communityService, identifiable, null, includingPropertySpecific );
    }

    @Override
    public List<? extends Issue> listWaivedIssues( CommunityService communityService, Identifiable identifiable, String property ) {
        return detectWaivedIssues( communityService, identifiable, property, true );
    }

    @Override
    public List<? extends Issue> listWaivedIssues( CommunityService communityService, Assignment assignment,
                                                   Boolean includingPropertySpecific ) {
        return detectWaivedIssues( communityService, assignment, includingPropertySpecific );
    }

    @Override
    public Boolean hasIssues( CommunityService communityService, Identifiable identifiable, String property ) {
        return !listIssues( communityService, identifiable, property ).isEmpty();
    }

    @Override
    public Boolean hasIssues( CommunityService communityService, Identifiable identifiable, Boolean includingPropertySpecific ) {
        return !listIssues( communityService, identifiable, includingPropertySpecific ).isEmpty();
    }

    @Override
    public Boolean hasUnwaivedIssues( CommunityService communityService, Identifiable identifiable, String property ) {
        return !listUnwaivedIssues( communityService, identifiable, property ).isEmpty();
    }

    @Override
    public Boolean hasUnwaivedIssues( CommunityService communityService, Identifiable identifiable,
                                      Boolean includingPropertySpecific ) {
        return !listUnwaivedIssues( communityService, identifiable, includingPropertySpecific ).isEmpty();
    }

    @Override
    public Boolean hasUnwaivedIssues( CommunityService communityService, Assignment assignment,
                                      Boolean includingPropertySpecific ) {
        return !listUnwaivedIssues( communityService, assignment, includingPropertySpecific ).isEmpty();
    }

    @Override
    public Boolean hasUserIssues( CommunityService communityService, Identifiable identifiable ) {
        return CollectionUtils.exists(
                this.listIssues( communityService, identifiable, false ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return !( (Issue) object ).isDetected();
                    }
                }
        );
    }

    @Override
    public String getIssuesSummary( CommunityService communityService, Identifiable identifiable,
                                    Boolean includingPropertySpecific ) {
        List<? extends Issue> issues = listUnwaivedIssues( communityService, identifiable, includingPropertySpecific );
        return summarize( issues );
    }

    @Override
    public String getIssuesOverview( CommunityService communityService, Identifiable identifiable,
                                     Boolean includingPropertySpecific ) {
        List<? extends Issue> issues = listUnwaivedIssues( communityService, identifiable, includingPropertySpecific );
        return makeOverview( issues );
    }


    @Override
    public String getIssuesSummary( CommunityService communityService, Assignment assignment,
                                    Boolean includingPropertySpecific ) {
        List<? extends Issue> issues = listUnwaivedIssues( communityService, assignment, includingPropertySpecific );
        return summarize( issues );
    }

    @Override
    public String getIssuesSummary( CommunityService communityService, Identifiable identifiable, String property ) {
        List<? extends Issue> issues = listUnwaivedIssues( communityService, identifiable, property );
        return summarize( issues );
    }

    /**
     * Aggregate the descriptions of issues.
     *
     * @param issues -- a list of issues
     * @return a string summarizing the issues
     */
    private String summarize( List<? extends Issue> issues ) {
        StringBuilder sb = new StringBuilder();
        for ( Issue issue : issues ) {
            sb.append( issue.getDescription() );
            if ( issues.indexOf( issue ) != issues.size() - 1 )
                sb.append( DESCRIPTION_SEPARATOR );
        }
        return sb.toString();
    }

    /**
     * Produce an overview of issues.
     *
     * @param issues -- a list of issues
     * @return a string providing an overview of the issues
     */
    private String makeOverview( List<? extends Issue> issues ) {
        StringBuilder sb = new StringBuilder();
        if ( issues.size() == 1 ) {
            Issue issue = issues.get( 0 );
            sb.append( issue.getType() )
                    .append( " issue: " )
                    .append( issue.getDescription() );
        } else {
            int validityCount = 0;
            int completenessCount = 0;
            int robustnessCount = 0;
            for ( Issue issue : issues ) {
                if ( issue.isValidity() ) validityCount++;
                else if ( issue.isCompleteness() ) completenessCount++;
                else if ( issue.isRobustness() ) robustnessCount++;
            }
            sb.append( "Issues: " );
            if ( validityCount > 0 )
                sb.append( validityCount ).append( " validity" );
            if ( completenessCount > 0 ) {
                sb.append( validityCount > 0
                        ? robustnessCount == 0
                        ? " and "
                        : ", "
                        : ""
                );
                sb.append( completenessCount ).append( " completeness" );
            }
            if ( robustnessCount > 0 ) {
                sb.append( validityCount > 0 || completenessCount > 0
                        ? " and "
                        : ""
                );
                sb.append( robustnessCount ).append( " robustness" );
            }
        }
        return sb.toString();
    }


    @Override
    public List<Issue> findAllIssuesFor( CommunityService communityService, ResourceSpec resource, Boolean specific ) {
        List<Issue> issues = new ArrayList<Issue>();
        if ( !resource.isAnyActor() )
            issues.addAll( listIssues( communityService, resource.getActor(), true ) );
        if ( !resource.isAnyOrganization() )
            issues.addAll( listIssues( communityService, resource.getOrganization(), true ) );
        if ( !resource.isAnyRole() )
            issues.addAll( listIssues( communityService, resource.getRole(), true ) );
        if ( !resource.isAnyJurisdiction() )
            issues.addAll( listIssues( communityService, resource.getJurisdiction(), true ) );
        issues.addAll( findAllIssuesInPlays( communityService, resource, specific ) );
        return issues;
    }

    @Override
    public Boolean isValid( CommunityService communityService, Identifiable identifiable ) {
        return test( communityService, identifiable, Issue.VALIDITY );
    }

    @Override
    public Boolean isComplete( CommunityService communityService, Identifiable identifiable ) {
        return test( communityService, identifiable, Issue.COMPLETENESS );
    }

    @Override
    public Boolean isRobust( CommunityService communityService, Identifiable identifiable ) {
        return test( communityService, identifiable, Issue.ROBUSTNESS );
    }

    private boolean test( CommunityService communityService, Identifiable identifiable, String test ) {
        return identifiable instanceof Plan ?
                passes( communityService, (Plan) identifiable, test ) :
                identifiable instanceof Segment ?
                        passes( communityService, (Segment) identifiable, test ) :
                        hasNoTestedIssue( communityService, identifiable, test );
    }

    private boolean passes( CommunityService communityService, Plan plan, String test ) {
        if ( !hasNoTestedIssue( communityService, plan, test ) )
            return false;

        for ( Segment segment : plan.getSegments() )
            if ( !passes( communityService, segment, test ) )
                return false;

        for ( ModelEntity entity : communityService.list( ModelEntity.class ) )
            if ( !hasNoTestedIssue( communityService, entity, test ) )
                return false;

        return true;
    }

    private boolean passes( CommunityService communityService, Segment segment, String test ) {
        if ( !hasNoTestedIssue( communityService, segment, test ) )
            return false;

        Iterator<Part> parts = segment.parts();
        while ( parts.hasNext() )
            if ( !hasNoTestedIssue( communityService, parts.next(), test ) )
                return false;

        Iterator<Flow> flows = segment.flows();
        while ( flows.hasNext() )
            if ( !hasNoTestedIssue( communityService, flows.next(), test ) )
                return false;
        return true;
    }

    private boolean hasNoTestedIssue( CommunityService communityService, Identifiable identifiable, final String test ) {
        return CollectionUtils.select( listUnwaivedIssues( communityService, identifiable, true ), new Predicate() {
            @Override
            public boolean evaluate( Object object ) {
                return ( (Issue) object ).getType().equals( test );
            }
        } ).isEmpty();
    }

    @Override
    public Integer countTestFailures( CommunityService communityService, Identifiable identifiable, String test ) {
        return identifiable instanceof Plan ?
                countFailures( communityService, (Plan) identifiable, test ) :
                identifiable instanceof Segment ?
                        countFailures( communityService, (Segment) identifiable, test ) :
                        countTestIssues( communityService, identifiable, test );
    }

    private int countFailures( CommunityService communityService, Plan plan, String test ) {
        int count = countTestIssues( communityService, plan, test );
        for ( Segment segment : plan.getSegments() )
            count += countFailures( communityService, segment, test );
        for ( ModelEntity entity : communityService.list( ModelEntity.class ) )
            count += countTestIssues( communityService, entity, test );
        return count;
    }

    private int countFailures( CommunityService communityService, Segment segment, String test ) {
        int count = countTestIssues( communityService, segment, test );
        Iterator<Part> parts = segment.parts();
        while ( parts.hasNext() )
            count += countTestIssues( communityService, parts.next(), test );
        Iterator<Flow> flows = segment.flows();
        while ( flows.hasNext() )
            count += countTestIssues( communityService, flows.next(), test );
        return count;
    }

    private int countTestIssues( CommunityService communityService, Identifiable identifiable, final String test ) {
        return CollectionUtils.select( listUnwaivedIssues( communityService, identifiable, true ), new Predicate() {
            @Override
            public boolean evaluate( Object object ) {
                return test.equals( ( (Issue) object ).getType() );
            }
        } ).size();
    }

    /**
     * Find the issues on parts and flows for all plays of a resource.
     *
     * @param communityService the query service
     * @param resourceSpec     a resource
     * @param specific         whether the match is "equals" or "narrows or equals"
     * @return a list of issues
     */
    private List<Issue> findAllIssuesInPlays( CommunityService communityService, ResourceSpec resourceSpec, boolean specific ) {
        List<Issue> issues = new ArrayList<Issue>();
        Set<Part> parts = new HashSet<Part>();
        for ( Play play : communityService.getPlanService().findAllPlays( resourceSpec, specific ) ) {
            parts.add( play.getPartFor( resourceSpec, communityService.getPlanService() ) );
            issues.addAll( listIssues( communityService, play.getFlow(), true ) );
        }
        for ( Part part : parts )
            issues.addAll( listIssues( communityService, part, true ) );
        return issues;
    }

    private List<Issue> detectAllIssues( CommunityService communityService, Identifiable identifiable, String property,
                                         boolean includingPropertySpecific ) {
        Detective detective = getDetective(  );
        List<Issue> issues = new ArrayList<Issue>();
        if ( property != null ) {
            issues.addAll( detective.detectUnwaivedPropertyIssues( communityService, identifiable, property ) );
            issues.addAll( detective.detectWaivedPropertyIssues( communityService, identifiable, property ) );
        } else {
            if ( includingPropertySpecific ) {
                issues.addAll( detective.detectUnwaivedIssues( communityService, identifiable, true ) );
                issues.addAll( detective.detectWaivedIssues( communityService, identifiable, true ) );
            }
            issues.addAll( detective.detectUnwaivedIssues( communityService, identifiable, false ) );
            issues.addAll( detective.detectWaivedIssues( communityService, identifiable, false ) );
        }
        return issues;
    }

    private List<? extends Issue> detectUnwaivedIssues( CommunityService communityService, Identifiable identifiable, String property,
                                                        boolean includingPropertySpecific ) {
        Detective detective = getDetective(  );
        if ( property == null ) {
            List<Issue> issues = new ArrayList<Issue>();
            if ( includingPropertySpecific )
                issues.addAll( detective.detectUnwaivedIssues( communityService, identifiable, true ) );
            issues.addAll( detective.detectUnwaivedIssues( communityService, identifiable, false ) );
            return issues;
        }

        return detective.detectUnwaivedPropertyIssues( communityService, identifiable, property );
    }

    private List<Issue> detectUnwaivedIssues( CommunityService communityService, Assignment assignment,
                                              boolean includingPropertySpecific ) {
        List<Issue> issues = new ArrayList<Issue>( detectUnwaivedIssues( communityService,
                assignment.getPart(),
                null,
                includingPropertySpecific ) );

        Actor actor = assignment.getActor();
        if ( actor != null && !actor.isUnknown() )
            issues.addAll( detectUnwaivedIssues( communityService, actor, null, includingPropertySpecific ) );
        Role role = assignment.getRole();
        if ( role != null && !role.isUnknown() )
            issues.addAll( detectUnwaivedIssues( communityService, role, null, includingPropertySpecific ) );
        Organization org = assignment.getOrganization();
        if ( org != null && !org.isUnknown() )
            issues.addAll( detectUnwaivedIssues( communityService, org, null, includingPropertySpecific ) );
        return issues;
    }

    private List<? extends Issue> detectWaivedIssues( CommunityService communityService, Identifiable identifiable, String property,
                                                      boolean includingPropertySpecific ) {
        Detective detective = getDetective(  );
        if ( property == null ) {
            List<Issue> issues = new ArrayList<Issue>();
            if ( includingPropertySpecific )
                issues.addAll( detective.detectWaivedIssues( communityService, identifiable, true ) );
            issues.addAll( detective.detectWaivedIssues( communityService, identifiable, false ) );
            return issues;
        }

        return detective.detectWaivedPropertyIssues( communityService, identifiable, property );
    }

    private List<Issue> detectWaivedIssues( CommunityService communityService, Assignment assignment,
                                            boolean includingPropertySpecific ) {
        List<Issue> issues = new ArrayList<Issue>( detectWaivedIssues( communityService,
                assignment.getPart(),
                null,
                includingPropertySpecific ) );

        Actor actor = assignment.getActor();
        if ( actor != null && !actor.isUnknown() )
            issues.addAll( detectWaivedIssues( communityService, actor, null, includingPropertySpecific ) );
        Role role = assignment.getRole();
        if ( role != null && !role.isUnknown() )
            issues.addAll( detectWaivedIssues( communityService, role, null, includingPropertySpecific ) );
        Organization org = assignment.getOrganization();
        if ( org != null && !org.isUnknown() )
            issues.addAll( detectWaivedIssues( communityService, org, null, includingPropertySpecific ) );
        return issues;
    }

    @Override
    public List<Issue> findAllIssues( CommunityService communityService ) {
        List<Issue> allIssues = new ArrayList<Issue>();
        if ( communityService.isForDomain() ) {
            for ( Identifiable identifiable : communityService.listKnownIdentifiables( ModelObject.class ) )
                allIssues.addAll( listIssues( communityService, identifiable, true ) );
            for ( Segment segment : communityService.list( Segment.class ) ) {
                Iterator<Part> parts = segment.parts();
                while ( parts.hasNext() )
                    allIssues.addAll( listIssues( communityService, parts.next(), true ) );
                Iterator<Flow> flows = segment.flows();
                while ( flows.hasNext() )
                    allIssues.addAll( listIssues( communityService, flows.next(), true ) );
            }
            for ( Identifiable identifiable : communityService.listKnownIdentifiables( ChannelsUser.class ) )
                allIssues.addAll( listIssues( communityService, identifiable, true ) );
        } else {
            for ( Identifiable identifiable : communityService.listKnownIdentifiables( Identifiable.class ) )
                allIssues.addAll( listIssues( communityService, identifiable, true ) );
        }
        return allIssues;
    }

    @Override
    public List<Issue> findAllUnwaivedIssues( CommunityService communityService ) {
        List<Issue> allUnwaivedIssues = new ArrayList<Issue>();
        if ( communityService.isForDomain() ) {
            for ( Identifiable identifiable : communityService.listKnownIdentifiables( ModelObject.class ) )
                allUnwaivedIssues.addAll( listUnwaivedIssues( communityService, identifiable, true ) );
            for ( Segment segment : communityService.list( Segment.class ) ) {
                Iterator<Part> parts = segment.parts();
                while ( parts.hasNext() )
                    allUnwaivedIssues.addAll( listUnwaivedIssues( communityService, parts.next(), true ) );
                Iterator<Flow> flows = segment.flows();
                while ( flows.hasNext() )
                    allUnwaivedIssues.addAll( listUnwaivedIssues( communityService, flows.next(), true ) );
            }
            for ( Identifiable identifiable : communityService.listKnownIdentifiables( ChannelsUser.class ) )
                allUnwaivedIssues.addAll( listUnwaivedIssues( communityService, identifiable, true ) );
        } else {
            for ( Identifiable identifiable : communityService.listKnownIdentifiables( Identifiable.class ) )
                allUnwaivedIssues.addAll( listUnwaivedIssues( communityService, identifiable, true ) );
        }
        return allUnwaivedIssues;
    }

    @Override
    public List<Issue> findAllWaivedIssues( CommunityService communityService ) {
        List<Issue> allWaivedIssues = new ArrayList<Issue>();
        if ( communityService.isForDomain() ) {
            for ( Identifiable identifiable : communityService.listKnownIdentifiables( ModelObject.class ) )
                allWaivedIssues.addAll( listWaivedIssues( communityService, identifiable, true ) );
            for ( Segment segment : communityService.list( Segment.class ) ) {
                Iterator<Part> parts = segment.parts();
                while ( parts.hasNext() )
                    allWaivedIssues.addAll( listWaivedIssues( communityService, parts.next(), true ) );
                Iterator<Flow> flows = segment.flows();
                while ( flows.hasNext() )
                    allWaivedIssues.addAll( listWaivedIssues( communityService, flows.next(), true ) );
            }
            for ( Identifiable identifiable : communityService.listKnownIdentifiables( ChannelsUser.class ) )
                allWaivedIssues.addAll( listWaivedIssues( communityService, identifiable, true ) );
        } else {
            for ( Identifiable identifiable : communityService.listKnownIdentifiables( Identifiable.class ) )
                allWaivedIssues.addAll( listWaivedIssues( communityService, identifiable, true ) );
        }
        return allWaivedIssues;
    }

    @Override
    public int unwaivedIssuesCount( Requirement requirement, CommunityService communityService ) {
        return detectUnwaivedIssues( communityService, requirement, null, true ).size();
    }

    @Override
    public int allIssuesCount( Requirement requirement, CommunityService communityService ) {
        return detectAllIssues( communityService, requirement, null, true ).size();
    }



}
