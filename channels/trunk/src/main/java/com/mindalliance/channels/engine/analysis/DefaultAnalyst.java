/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.engine.analysis;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.Command;
import com.mindalliance.channels.core.command.Commander;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Assignment;
import com.mindalliance.channels.core.model.Commitment;
import com.mindalliance.channels.core.model.ExternalFlow;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.ModelEntity.Kind;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.model.Requirement;
import com.mindalliance.channels.core.model.ResourceSpec;
import com.mindalliance.channels.core.model.Role;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.model.TransmissionMedium;
import com.mindalliance.channels.core.query.Commitments;
import com.mindalliance.channels.core.query.PlanService;
import com.mindalliance.channels.core.query.Play;
import com.mindalliance.channels.core.util.ChannelsUtils;
import com.mindalliance.channels.engine.analysis.graph.EntityRelationship;
import com.mindalliance.channels.engine.analysis.graph.SegmentRelationship;
import com.mindalliance.channels.engine.imaging.ImagingService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.Lifecycle;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Default implementation of Analyst.
 */
public class DefaultAnalyst implements Analyst, Lifecycle {

    /**
     * Description separator.
     */
    private static final String DESCRIPTION_SEPARATOR = " -- ";

    /**
     * The detective service.
     */
    private Detective detective;

    /**
     * Low priority, multi-threaded issues scanner.
     */
    private IssueScanner issueScanner;

    private ImagingService imagingService;

    /**
     * Lifecycle status.
     */
    private boolean running;

    public DefaultAnalyst() {
    }

    /**
     * Set the detective to use.
     *
     * @param detective an issue detector manager
     */
    public void setDetective( Detective detective ) {
        this.detective = detective;
    }

    @Override
    public void setIssueScanner( IssueScanner issueScanner ) {
        this.issueScanner = issueScanner;
    }

    /**
     * Enable analysis of problems in active plans.
     */
    @Override
    public void start() {
        running = true;
        // onStart();
    }

    /**
     * Disable analysis of problems in active plans.
     */
    @Override
    public void stop() {
        issueScanner.terminate();
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public void onAfterCommand( PlanCommunity planCommunity ) {
        issueScanner.rescan( planCommunity );
    }

    @Override
    public void onStart( PlanCommunity planCommunity ) {
        issueScanner.scan( planCommunity );
    }

    @Override
    public void onStop() {
        stop();
    }

    @Override
    public void onDestroy() {
        issueScanner.terminate();
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

    /**
     * Get the imaging service.
     *
     * @return the imaging service
     */
    @Override
    public ImagingService getImagingService() {
        return imagingService;
    }

    public void setImagingService( ImagingService imagingService ) {
        this.imagingService = imagingService;
    }

    @Override
    public List<Issue> findAllIssues( CommunityService communityService ) {
        List<Issue> allIssues = new ArrayList<Issue>();
        if ( communityService.isForDomain() ) {
            for ( Identifiable identifiable : communityService.listIdentifiables( ModelObject.class ) )
                allIssues.addAll( listIssues( communityService, identifiable, true ) );
            for ( Segment segment : communityService.list( Segment.class ) ) {
                Iterator<Part> parts = segment.parts();
                while ( parts.hasNext() )
                    allIssues.addAll( listIssues( communityService, parts.next(), true ) );
                Iterator<Flow> flows = segment.flows();
                while ( flows.hasNext() )
                    allIssues.addAll( listIssues( communityService, flows.next(), true ) );
            }
        } else {
            for ( Identifiable identifiable : communityService.listIdentifiables( Identifiable.class ) )
                allIssues.addAll( listIssues( communityService, identifiable, true ) );
        }
        return allIssues;
    }

    @Override
    public List<Issue> findAllUnwaivedIssues( CommunityService communityService ) {
        List<Issue> allUnwaivedIssues = new ArrayList<Issue>();
        if ( communityService.isForDomain() ) {
            for ( Identifiable identifiable : communityService.listIdentifiables( ModelObject.class ) )
                allUnwaivedIssues.addAll( listUnwaivedIssues( communityService, identifiable, true ) );
            for ( Segment segment : communityService.list( Segment.class ) ) {
                Iterator<Part> parts = segment.parts();
                while ( parts.hasNext() )
                    allUnwaivedIssues.addAll( listUnwaivedIssues( communityService, parts.next(), true ) );
                Iterator<Flow> flows = segment.flows();
                while ( flows.hasNext() )
                    allUnwaivedIssues.addAll( listUnwaivedIssues( communityService, flows.next(), true ) );
            }
        } else {
            for ( Identifiable identifiable : communityService.listIdentifiables( Identifiable.class ) )
                allUnwaivedIssues.addAll( listUnwaivedIssues( communityService, identifiable, true ) );
        }
        return allUnwaivedIssues;
    }

    @Override
    public List<Issue> findAllWaivedIssues( CommunityService communityService ) {
        List<Issue> allWaivedIssues = new ArrayList<Issue>();
        if ( communityService.isForDomain() ) {
            for ( Identifiable identifiable : communityService.listIdentifiables( ModelObject.class ) )
                allWaivedIssues.addAll( listWaivedIssues( communityService, identifiable, true ) );
            for ( Segment segment : communityService.list( Segment.class ) ) {
                Iterator<Part> parts = segment.parts();
                while ( parts.hasNext() )
                    allWaivedIssues.addAll( listWaivedIssues( communityService, parts.next(), true ) );
                Iterator<Flow> flows = segment.flows();
                while ( flows.hasNext() )
                    allWaivedIssues.addAll( listWaivedIssues( communityService, flows.next(), true ) );
            }
        } else {
            for ( Identifiable identifiable : communityService.listIdentifiables( Identifiable.class ) )
                allWaivedIssues.addAll( listWaivedIssues( communityService, identifiable, true ) );
        }
        return allWaivedIssues;
    }

    @Override
    public SegmentRelationship findSegmentRelationship( CommunityService communityService, Segment fromSegment,
                                                        Segment toSegment ) {
        List<ExternalFlow> externalFlows = new ArrayList<ExternalFlow>();
        List<Part> initiators = new ArrayList<Part>();
        List<Part> terminators = new ArrayList<Part>();
        Iterator<Flow> flows = fromSegment.flows();
        while ( flows.hasNext() ) {
            Flow flow = flows.next();
            if ( flow.isExternal() ) {
                ExternalFlow externalFlow = (ExternalFlow) flow;
                if ( externalFlow.getConnector().getSegment() == toSegment && !externalFlow.isPartTargeted() )
                    externalFlows.add( externalFlow );
            }
        }
        flows = toSegment.flows();
        while ( flows.hasNext() ) {
            Flow flow = flows.next();
            if ( flow.isExternal() ) {
                ExternalFlow externalFlow = (ExternalFlow) flow;
                if ( externalFlow.getConnector().getSegment() == fromSegment && externalFlow.isPartTargeted() )
                    externalFlows.add( externalFlow );
            }
        }
        for ( Part part : communityService.getPlanService().findInitiators( toSegment ) )
            if ( part.getSegment().equals( fromSegment ) )
                initiators.add( part );
        for ( Part part : communityService.getPlanService().findExternalTerminators( toSegment ) )
            if ( part.getSegment().equals( fromSegment ) )
                terminators.add( part );
        if ( externalFlows.isEmpty() && initiators.isEmpty() && terminators.isEmpty() )
            return null;
        else {
            SegmentRelationship segmentRelationship = new SegmentRelationship( fromSegment, toSegment );
            segmentRelationship.setExternalFlows( externalFlows );
            segmentRelationship.setInitiators( initiators );
            segmentRelationship.setTerminators( terminators );
            return segmentRelationship;
        }
    }


    @Override
    public Boolean canBeRealized( Commitment commitment, Plan plan, CommunityService communityService ) {
        return findRealizabilityProblems( plan, commitment, communityService ).isEmpty();
    }


    @Override
    public <T extends ModelEntity> EntityRelationship<T> findEntityRelationship( CommunityService communityService,
                                                                                 T fromEntity, T toEntity ) {
        return findEntityRelationshipInPlan( communityService, fromEntity, toEntity, null );
    }

    @Override
    public <T extends ModelEntity> EntityRelationship<T> findEntityRelationshipInPlan( CommunityService communityService,
                                                                                       T fromEntity, T toEntity,
                                                                                       Segment segment ) {
        Place planLocale = communityService.getPlanService().getPlanLocale();
        Commitments commitments = Commitments.all( communityService.getPlanService() )
                .inSegment( segment )
                .withEntityCommitting( fromEntity, planLocale )
                .withEntityBenefiting( toEntity, planLocale );
        Set<Flow> entityFlows = new HashSet<Flow>();
        for ( Commitment commitment : commitments ) {
            entityFlows.add( commitment.getSharing() );
        }
        if ( entityFlows.isEmpty() )
            return null;
        else {
            EntityRelationship<T> entityRel = new EntityRelationship<T>( fromEntity, toEntity );
            entityRel.setFlows( new ArrayList<Flow>( entityFlows ) );
            return entityRel;
        }
    }

    @Override
    public List<EntityRelationship> findEntityRelationshipsInPlan( CommunityService communityService, Segment segment,
                                                                   Class<? extends ModelEntity> entityClass, Kind kind ) {
        PlanService planService = communityService.getPlanService();
        List<EntityRelationship> rels = new ArrayList<EntityRelationship>();
        List<? extends ModelEntity> entities =
                kind == Kind.Actual
                        ? planService.listActualEntities( entityClass )
                        : planService.listTypeEntities( entityClass );
        for ( ModelEntity entity : entities ) {
            if ( !entity.isUnknown() )
                rels.addAll( findEntityRelationshipsInPlan( segment, entity, communityService ) );
        }
        return rels;
    }

    @Override
    public List<EntityRelationship> findEntityRelationshipsInPlan( Segment segment, ModelEntity entity,
                                                                   CommunityService communityService ) {
        PlanService planService = communityService.getPlanService();
        List<EntityRelationship> rels = new ArrayList<EntityRelationship>();
        Place planLocale = planService.getPlanLocale();
        // Committing relationships
        Commitments entityCommittingCommitments = Commitments.all( planService )
                .inSegment( segment )
                .withEntityCommitting( entity, planLocale );
        List<? extends ModelEntity> otherEntities =
                entity.isActual()
                        ? planService.listActualEntities( entity.getClass() )
                        : planService.listTypeEntities( entity.getClass() );

        for ( ModelEntity otherEntity : otherEntities ) {
            if ( !otherEntity.isUnknown() && !entity.equals( otherEntity ) ) {
                Set<Flow> flows = new HashSet<Flow>();
                for ( Commitment commitment : entityCommittingCommitments.withEntityBenefiting( otherEntity, planLocale ) ) {
                    flows.add( commitment.getSharing() );
                }
                if ( !flows.isEmpty() ) {
                    EntityRelationship<ModelEntity> rel = new EntityRelationship<ModelEntity>( entity, otherEntity );
                    rel.setFlows( new ArrayList<Flow>( flows ) );
                    rels.add( rel );
                }
            }
        }
        // Benefiting relationships
        Commitments entityBenefitingCommitments = Commitments.all( planService )
                .inSegment( segment )
                .withEntityBenefiting( entity, planLocale );
        for ( ModelEntity otherEntity : otherEntities ) {
            if ( !otherEntity.isUnknown() && !entity.equals( otherEntity ) ) {
                Set<Flow> flows = new HashSet<Flow>();
                for ( Commitment commitment : entityBenefitingCommitments.withEntityCommitting( otherEntity, planLocale ) ) {
                    flows.add( commitment.getSharing() );
                }
                if ( !flows.isEmpty() ) {
                    EntityRelationship<ModelEntity> rel = new EntityRelationship<ModelEntity>( otherEntity, entity );
                    rel.setFlows( new ArrayList<Flow>( flows ) );
                    rels.add( rel );
                }
            }
        }
        return rels;
    }

    @Override
    public Boolean isEffectivelyConceptualInPlan( CommunityService communityService, Part part ) {
        return !findConceptualCausesInPlan( communityService, part ).isEmpty();
    }

    @Override
    public List<String> findConceptualCausesInPlan( CommunityService communityService, Part part ) {
        List<String> causes = new ArrayList<String>();
        if ( part.isProhibited() )
            causes.add( "prohibited" );
        List<Assignment> assignments = communityService.getPlanService().findAllAssignments( part, false );
        if ( assignments.isEmpty() )
            causes.add( "no agent is assigned to the task" );
        else if ( noAvailability( assignments ) )
            causes.add( "none of the assigned agents is ever available" );
        return causes;
    }

    @Override
    public List<String> findConceptualRemediationsInPlan( CommunityService communityService, Part part ) {
        List<String> remediations = new ArrayList<String>();
        if ( part.isProhibited() )
            remediations.add( "remove the prohibition" );
        List<Assignment> assignments = communityService.getPlanService().findAllAssignments( part, false );
        if ( assignments.isEmpty() ) {
            remediations.add( "explicitly assign an agent to the task" );
            remediations.add( "profile an agent to match the task specifications" );
            remediations.add( "modify the task specifications so that it matches at least one agent" );
        } else if ( noAvailability( assignments ) ) {
            remediations.add( "make assigned agents available" );
        }
        return remediations;
    }

    @Override
    public Boolean isEffectivelyConceptualInPlan( CommunityService communityService, Flow flow ) {
        return !flow.isToSelf() && !findConceptualCausesInPlan( communityService, flow ).isEmpty();
    }

    @Override
    public List<String> findConceptualCausesInPlan( CommunityService communityService, Flow flow ) {
        PlanService planService = communityService.getPlanService();
        List<String> causes = new ArrayList<String>();
        if ( flow.isProhibited() )
            causes.add( "prohibited" );
        if ( flow.isNeed() && isEffectivelyConceptualInPlan( communityService, (Part) flow.getTarget() ) )
            causes.add( "this task can not be executed" );
        else if ( flow.isCapability() && isEffectivelyConceptualInPlan( communityService, (Part) flow.getSource() ) )
            causes.add( "this can not be executed" );
        else if ( flow.isSharing() ) {
            if ( isEffectivelyConceptualInPlan( communityService, (Part) flow.getSource() ) )
                causes.add( "the task \"" + ( (Part) flow.getSource() ).getTask() + "\" can not be executed" );
            if ( isEffectivelyConceptualInPlan( communityService, (Part) flow.getTarget() ) )
                causes.add( "the task \"" + ( (Part) flow.getTarget() ).getTask() + "\" can not be executed" );
            if ( flow.getEffectiveChannels().isEmpty() )
                causes.add( "no channels is identified" );
            else {
                List<Commitment> commitments =
                        planService.findAllCommitments( flow, false, planService.getAssignments( false ) );
                if ( commitments.isEmpty() ) {
                    causes.add( "there are no communication commitments between any pair of agents" );
                } else {
                    StringBuilder sb = new StringBuilder();
                    Plan plan = communityService.getPlan();
                    Place locale = planService.getPlanLocale();
                    List<Commitment> agreedTo = agreedToFilter( commitments, communityService.getPlanService() );
                    if ( agreedTo.isEmpty() ) {
                        sb.append( "none of the communication commitments are agreed to as required " );
                    } else {
                        List<TransmissionMedium> mediaUsed = flow.transmissionMedia();
                        List<Commitment> availabilitiesCoincideIfRequired =
                                availabilitiesCoincideIfRequiredFilter( agreedTo, mediaUsed, locale );
                        if ( availabilitiesCoincideIfRequired.isEmpty() ) {
                            sb.append( "in all communication commitments, " );
                            sb.append( "agents are never available at the same time as they must to communicate" );
                        } /*else {
                            List<Commitment> mediaDeployed =
                                    someMediaDeployedFilter( availabilitiesCoincideIfRequired, mediaUsed, locale );
                            if ( mediaDeployed.isEmpty() ) {
                                if ( sb.length() == 0 )
                                    sb.append( "in all communication commitments, " );
                                else
                                    sb.append( ", or " );
                                sb.append( "agents do not have access to required transmission media" );
                            }*/ else {
                            List<Commitment> reachable =
                                    reachableFilter( availabilitiesCoincideIfRequired, mediaUsed, locale );
                            if ( reachable.isEmpty() ) {
                                if ( sb.length() == 0 )
                                    sb.append( "in all communication commitments, " );
                                else
                                    sb.append( ", or " );
                                sb.append( "the agent to be contacted is not reachable (no contact info)" );
                            } else {
                                List<Commitment> agentsQualified =
                                        agentsQualifiedFilter( reachable, mediaUsed, locale );
                                if ( agentsQualified.isEmpty() ) {
                                    if ( sb.length() == 0 )
                                        sb.append( "in all communication commitments, " );
                                    else
                                        sb.append( ", or " );
                                    sb.append( "both agents are not qualified to use a transmission medium" );
                                } else {
                                    List<Commitment> languageOverlap = commonLanguageFilter( plan, agentsQualified );
                                    if ( languageOverlap.isEmpty() ) {
                                        if ( sb.length() == 0 )
                                            sb.append( "in all communication commitments, " );
                                        else
                                            sb.append( ", or " );
                                        sb.append( "agents do not speak a common language" );
                                    }
                                }
                                //            }
                            }
                        }
                        if ( sb.length() > 0 ) {
                            causes.add( sb.toString() );
                        }
                    }
                }
            }
        }
        return causes;
    }

    @Override
    public List<String> findConceptualRemediationsInPlan( CommunityService communityService, Flow flow ) {
        PlanService planService = communityService.getPlanService();
        List<String> remediations = new ArrayList<String>();
        if ( flow.isProhibited() )
            remediations.add( "remove the prohibition" );
        if ( flow.isNeed() && isEffectivelyConceptualInPlan( communityService, (Part) flow.getTarget() ) )
            remediations.add( "make this task executable" );
        else if ( flow.isCapability() && isEffectivelyConceptualInPlan( communityService, (Part) flow.getSource() ) )
            remediations.add( "make this task executable" );
        else if ( flow.isSharing() ) {
            if ( isEffectivelyConceptualInPlan( communityService, (Part) flow.getSource() ) )
                remediations.add( "make the task \"" + ( (Part) flow.getSource() ).getTask() + "\" executable" );
            if ( isEffectivelyConceptualInPlan( communityService, (Part) flow.getTarget() ) )
                remediations.add( "make the task \"" + ( (Part) flow.getTarget() ).getTask() + "\" executable" );
            if ( flow.getEffectiveChannels().isEmpty() )
                remediations.add( "add at least one channel to the flow" );
            else {
                List<Commitment> commitments = planService.findAllCommitments( flow );
                if ( commitments.isEmpty() ) {
                    remediations.add(
                            "change the definitions of the source and/or target tasks so that agents are assigned to both" );
                    remediations.add(
                            "add jobs to relevant organizations so that agents can be assigned to source and/or target tasks" );
                } else {
                    Plan plan = communityService.getPlan();
                    Place locale = planService.getPlanLocale();
                    List<Commitment> agreedTo = agreedToFilter( commitments, planService );
                    if ( agreedTo.isEmpty() ) {
                        remediations.add( "change the profile of the committing organizations and " +
                                "add the required agreements or remove the requirement for agreements" );
                    } else {
                        List<TransmissionMedium> mediaUsed = flow.transmissionMedia();
                        List<Commitment> availabilitcoincideIfRequired =
                                availabilitiesCoincideIfRequiredFilter( agreedTo, mediaUsed, locale );
                        if ( availabilitcoincideIfRequired.isEmpty() ) {
                            remediations.add( "change agent availability to make them coincide" );
                            remediations.add( "add a channel that does not require synchronous communication" );
                        }/* else {
                            List<Commitment> mediaDeployed =
                                    someMediaDeployedFilter( availabilitcoincideIfRequired, mediaUsed, locale );
                            if ( mediaDeployed.isEmpty() )
                                remediations.add( "make sure that the agents that are available"
                                        + " to each other also have access to required transmission media" );
*/ else {
                            List<Commitment> reachable =
                                    reachableFilter( availabilitcoincideIfRequired, mediaUsed, locale );
                            if ( reachable.isEmpty() )
                                remediations.add( "make sure that the agents that are available"
                                        + " to each other also have known contact information" );
                            else {
                                List<Commitment> agentsQualified =
                                        agentsQualifiedFilter( reachable, mediaUsed, locale );
                                if ( agentsQualified.isEmpty() ) {
                                    remediations.add( "make sure that agents that are available to each other"
                                            + " and reachable are also qualified to use the transmission media" );
                                    remediations.add( "add channels with transmission media requiring no qualification" );
                                } else if ( commonLanguageFilter( plan, commitments ).isEmpty() ) {
                                    remediations.add( "make sure that agents that are available to each other, "
                                            + "reachable and qualified to use the transmission media "
                                            + "can also speak a common language" );
                                }
                                //            }
                            }
                        }
                    }
                }
            }
        }
        return remediations;
    }

    // Filter commitments where agreements are in place if required.
    @SuppressWarnings( "unchecked" )
    private List<Commitment> agreedToFilter( List<Commitment> commitments, final PlanService planService ) {
        return (List<Commitment>) CollectionUtils.select( commitments, new Predicate() {
            @Override
            public boolean evaluate( Object object ) {
                return planService.isAgreedToIfRequired( (Commitment) object );
            }
        } );
    }

    // Filter commitments where agent availabilities coincide if required.
    @SuppressWarnings( "unchecked" )
    private List<Commitment> availabilitiesCoincideIfRequiredFilter( List<Commitment> commitments,
                                                                     final List<TransmissionMedium> mediaUsed,
                                                                     final Place planLocale ) {
        return (List<Commitment>) CollectionUtils.select( commitments, new Predicate() {
            @Override
            public boolean evaluate( Object object ) {
                return isAvailabilitiesCoincideIfRequired( (Commitment) object, mediaUsed, planLocale );
            }
        } );
    }

    @Override
    public Boolean isAvailabilitiesCoincideIfRequired( Commitment commitment, List<TransmissionMedium> mediaUsed,
                                                       final Place planLocale ) {
        Actor committer = commitment.getCommitter().getActor();
        Actor beneficiary = commitment.getBeneficiary().getActor();
//        boolean coincide = committer.getAvailability().equals( beneficiary.getAvailability() );
        boolean coincide = beneficiary.getAvailability().includes( committer.getAvailability() );
        // agent availabilities coincide or there is at least one medium used that is not synchronous
        return !mediaUsed.isEmpty() && ( coincide || CollectionUtils.exists( mediaUsed, new Predicate() {
            @Override
            public boolean evaluate( Object object ) {
                return !( (TransmissionMedium) object ).isEffectiveSynchronous( planLocale );
            }
        } ) );
    }

/*
    // Filter commitments where agent have access to required transmission media.
    @SuppressWarnings( "unchecked" )
    private List<Commitment> someMediaDeployedFilter( List<Commitment> commitments,
                                                      final List<TransmissionMedium> mediaUsed,
                                                      final Place planLocale ) {
        return (List<Commitment>) CollectionUtils.select( commitments, new Predicate() {
            @Override
            public boolean evaluate( Object object ) {
                Commitment commitment = (Commitment) object;
                return isSomeMediaDeployed( commitment, mediaUsed, planLocale );
            }
        } );
    }
*/

/*
    @Override
    public boolean isSomeMediaDeployed( final Commitment commitment, List<TransmissionMedium> mediaUsed,
                                        final Place planLocale ) {
        return CollectionUtils.exists( mediaUsed, new Predicate() {
            @Override
            public boolean evaluate( Object object ) {
                TransmissionMedium medium = (TransmissionMedium) object;
                return commitment.getCommitter().getOrganization().isMediumDeployed( medium, planLocale )
                        && commitment.getBeneficiary().getOrganization().isMediumDeployed( medium, planLocale );
            }
        } );
    }
*/

    // Filter commitments where agent to eb contacted has known contact info.
    @SuppressWarnings( "unchecked" )
    private List<Commitment> reachableFilter( List<Commitment> commitments, final List<TransmissionMedium> mediaUsed,
                                              final Place planLocale ) {
        return (List<Commitment>) CollectionUtils.select( commitments, new Predicate() {
            @Override
            public boolean evaluate( Object object ) {
                return isReachable( (Commitment) object, mediaUsed, planLocale );
            }
        } );
    }

    @Override
    public boolean isReachable( Commitment commitment, List<TransmissionMedium> mediaUsed, final Place planLocale ) {
        boolean isRequest = commitment.getSharing().isAskedFor();
        final Actor receiver =
                isRequest ? commitment.getCommitter().getActor() : commitment.getBeneficiary().getActor();
        return CollectionUtils.exists( mediaUsed, new Predicate() {
            @Override
            public boolean evaluate( Object object ) {
                TransmissionMedium medium = (TransmissionMedium) object;
                return !medium.requiresAddress() || !medium.isUnicast() || receiver.hasChannelFor( medium, planLocale );
            }
        } );
    }

    // Filter commitments where both agents are qualified to use a transmission medium.
    @SuppressWarnings( "unchecked" )
    private List<Commitment> agentsQualifiedFilter( List<Commitment> commitments,
                                                    final List<TransmissionMedium> mediaUsed, final Place planLocale ) {
        return (List<Commitment>) CollectionUtils.select( commitments, new Predicate() {
            @Override
            public boolean evaluate( Object object ) {
                return isAgentsQualified( (Commitment) object, mediaUsed, planLocale );
            }
        } );
    }

    @Override
    public Boolean isAgentsQualified( final Commitment commitment, List<TransmissionMedium> mediaUsed,
                                      final Place planLocale ) {
        return CollectionUtils.exists( mediaUsed, new Predicate() {
            @Override
            public boolean evaluate( Object object ) {
                TransmissionMedium medium = (TransmissionMedium) object;
                return medium.getQualification() == null
                        || commitment.getCommitter().getActor().narrowsOrEquals( medium.getQualification(), planLocale )
                        && commitment.getBeneficiary().getActor().narrowsOrEquals( medium.getQualification(),
                        planLocale );
            }
        } );
    }

    // Filter commitments where agents can understand one another.
    @SuppressWarnings( "unchecked" )
    private static List<Commitment> commonLanguageFilter( final Plan plan, List<Commitment> commitments ) {
        return (List<Commitment>) CollectionUtils.select( commitments, new Predicate() {
            @Override
            public boolean evaluate( Object object ) {
                return ( (Commitment) object ).isCommonLanguage( plan );
            }
        } );
    }

    @Override
    public String realizability( Commitment commitment, CommunityService communityService ) {
        PlanService planService = communityService.getPlanService();
        List<String> problems = findRealizabilityProblems( planService.getPlan(), commitment, communityService );
        return problems.isEmpty() ?
                "Yes" :
                "No: " + StringUtils.capitalize( ChannelsUtils.listToString( problems, ", and " ) );
    }

    @Override
    public List<String> findRealizabilityProblems( Plan plan, Commitment commitment, CommunityService communityService ) {
        List<String> problems = new ArrayList<String>();
        List<TransmissionMedium> mediaUsed = commitment.getSharing().transmissionMedia();
        Place planLocale = communityService.getPlanService().getPlanLocale();
/*
        if ( !communityService.isAgreedToIfRequired( commitment ) )
            problems.add( "sharing not agreed to as required" );
*/
        if ( !isAvailabilitiesCoincideIfRequired( commitment, mediaUsed, planLocale ) )
            problems.add( "availabilities do not coincide as they must" );
        if ( !commitment.isCommonLanguage( plan ) )
            problems.add( "no common language" );
        if ( commitment.getSharing().getEffectiveChannels().isEmpty() )
            problems.add( "no channel is identified" );
        else {
 /*           if ( !isSomeMediaDeployed( commitment, mediaUsed, planLocale ) )
                problems.add( "no access to required transmission media" );
*/
            if ( !isAgentsQualified( commitment, mediaUsed, planLocale ) )
                problems.add( "insufficient technical qualification" );
            /*if ( !isReachable( commitment, mediaUsed, planLocale ) )
                problems.add( "missing contact info" );*/    // contact info will be supplied by participating user
        }
        return problems;
    }

    // There is no assigned agent available at any time.
    private boolean noAvailability( List<Assignment> assignments ) {
        boolean noAvailability = !CollectionUtils.exists( assignments, new Predicate() {
            @Override
            public boolean evaluate( Object object ) {
                Assignment assignment = (Assignment) object;
                return !assignment.getActor().getAvailability().isEmpty();
            }
        } );
        return !assignments.isEmpty() && noAvailability;
    }

    @Override
    public int unwaivedIssuesCount( Requirement requirement, CommunityService communityService ) {
        return detectUnwaivedIssues( communityService, requirement, null, true ).size();
    }

    @Override
    public int allIssuesCount( Requirement requirement, CommunityService communityService ) {
        return detectAllIssues( communityService, requirement, null, true ).size();
    }

    @Override
    public void commandDone( Commander commander, Command command, Change change ) {
        if ( !commander.isReplaying() && command.isTop() && !change.isNone() )
            onAfterCommand( commander.getPlanCommunity() );
    }

    @Override
    public void commandUndone( Commander commander, Command command, Change change ) {
        commandDone( commander, command, change );
    }

    @Override
    public void commandRedone( Commander commander, Command command, Change change ) {
        commandDone( commander, command, change );
    }

    @Override
    public void started( Commander commander ) {
        onStart( commander.getPlanCommunity() );
    }
}
