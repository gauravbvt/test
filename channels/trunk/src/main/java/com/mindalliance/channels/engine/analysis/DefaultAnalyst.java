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
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Assignment;
import com.mindalliance.channels.core.model.Commitment;
import com.mindalliance.channels.core.model.ExternalFlow;
import com.mindalliance.channels.core.model.Flow;
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
import com.mindalliance.channels.core.query.QueryService;
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
    public void onAfterCommand( Plan plan ) {
        issueScanner.rescan( plan );
    }

    @Override
    public void onStart( Plan plan ) {
        issueScanner.scan( plan );
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
    public List<? extends Issue> listIssues( QueryService queryService, ModelObject modelObject,
                                             Boolean includingPropertySpecific, Boolean includingWaived ) {
        return includingWaived ?
                detectAllIssues( queryService, modelObject, null, includingPropertySpecific ) :
                detectUnwaivedIssues( queryService, modelObject, null, includingPropertySpecific );
    }

    @Override
    public List<Issue> listIssues( QueryService queryService, ModelObject modelObject,
                                   Boolean includingPropertySpecific ) {
        return detectAllIssues( queryService, modelObject, null, includingPropertySpecific );
    }

    @Override
    public List<Issue> listIssues( QueryService queryService, ModelObject modelObject, String property ) {
        return detectAllIssues( queryService, modelObject, property, true );
    }

    @Override
    public List<? extends Issue> listUnwaivedIssues( QueryService queryService, ModelObject modelObject,
                                                     Boolean includingPropertySpecific ) {
        return detectUnwaivedIssues( queryService, modelObject, null, includingPropertySpecific );
    }

    @Override
    public List<? extends Issue> listUnwaivedIssues( QueryService queryService, ModelObject modelObject, String property ) {
        return detectUnwaivedIssues( queryService, modelObject, property, true );
    }

    @Override
    public List<? extends Issue> listUnwaivedIssues( QueryService queryService, Assignment assignment,
                                                     Boolean includingPropertySpecific ) {
        return detectUnwaivedIssues( queryService, assignment, includingPropertySpecific );
    }

    @Override
    public List<? extends Issue> listWaivedIssues( QueryService queryService, ModelObject modelObject,
                                                   Boolean includingPropertySpecific ) {
        return detectWaivedIssues( queryService, modelObject, null, includingPropertySpecific );
    }

    @Override
    public List<? extends Issue> listWaivedIssues( QueryService queryService, ModelObject modelObject, String property ) {
        return detectWaivedIssues( queryService, modelObject, property, true );
    }

    @Override
    public List<? extends Issue> listWaivedIssues( QueryService queryService, Assignment assignment,
                                                   Boolean includingPropertySpecific ) {
        return detectWaivedIssues( queryService, assignment, includingPropertySpecific );
    }

    @Override
    public Boolean hasIssues( QueryService queryService, ModelObject modelObject, String property ) {
        return !listIssues( queryService, modelObject, property ).isEmpty();
    }

    @Override
    public Boolean hasIssues( QueryService queryService, ModelObject modelObject, Boolean includingPropertySpecific ) {
        return !listIssues( queryService, modelObject, includingPropertySpecific ).isEmpty();
    }

    @Override
    public Boolean hasUnwaivedIssues( QueryService queryService, ModelObject modelObject, String property ) {
        return !listUnwaivedIssues( queryService, modelObject, property ).isEmpty();
    }

    @Override
    public Boolean hasUnwaivedIssues( QueryService queryService, ModelObject modelObject,
                                      Boolean includingPropertySpecific ) {
        return !listUnwaivedIssues( queryService, modelObject, includingPropertySpecific ).isEmpty();
    }

    @Override
    public Boolean hasUnwaivedIssues( QueryService queryService, Assignment assignment,
                                      Boolean includingPropertySpecific ) {
        return !listUnwaivedIssues( queryService, assignment, includingPropertySpecific ).isEmpty();
    }

    @Override
    public Boolean hasUserIssues( QueryService queryService, ModelObject modelObject ) {
        return CollectionUtils.exists(
                this.listIssues( queryService, modelObject, false ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return !( (Issue) object ).isDetected();
                    }
                }
        );
    }

    @Override
    public String getIssuesSummary( QueryService queryService, ModelObject modelObject,
                                    Boolean includingPropertySpecific ) {
        List<? extends Issue> issues = listUnwaivedIssues( queryService, modelObject, includingPropertySpecific );
        return summarize( issues );
    }

    @Override
    public String getIssuesOverview( QueryService queryService, ModelObject modelObject,
                                     Boolean includingPropertySpecific ) {
        List<? extends Issue> issues = listUnwaivedIssues( queryService, modelObject, includingPropertySpecific );
        return makeOverview( issues );
    }


    @Override
    public String getIssuesSummary( QueryService queryService, Assignment assignment,
                                    Boolean includingPropertySpecific ) {
        List<? extends Issue> issues = listUnwaivedIssues( queryService, assignment, includingPropertySpecific );
        return summarize( issues );
    }

    @Override
    public String getIssuesSummary( QueryService queryService, ModelObject modelObject, String property ) {
        List<? extends Issue> issues = listUnwaivedIssues( queryService, modelObject, property );
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
    public List<Issue> findAllIssuesFor( QueryService queryService, ResourceSpec resource, Boolean specific ) {
        List<Issue> issues = new ArrayList<Issue>();
        if ( !resource.isAnyActor() )
            issues.addAll( listIssues( queryService, resource.getActor(), true ) );
        if ( !resource.isAnyOrganization() )
            issues.addAll( listIssues( queryService, resource.getOrganization(), true ) );
        if ( !resource.isAnyRole() )
            issues.addAll( listIssues( queryService, resource.getRole(), true ) );
        if ( !resource.isAnyJurisdiction() )
            issues.addAll( listIssues( queryService, resource.getJurisdiction(), true ) );
        issues.addAll( findAllIssuesInPlays( queryService, resource, specific ) );
        return issues;
    }

    @Override
    public Boolean isValid( QueryService queryService, ModelObject modelObject ) {
        return test( queryService, modelObject, Issue.VALIDITY );
    }

    @Override
    public Boolean isComplete( QueryService queryService, ModelObject modelObject ) {
        return test( queryService, modelObject, Issue.COMPLETENESS );
    }

    @Override
    public Boolean isRobust( QueryService queryService, ModelObject modelObject ) {
        return test( queryService, modelObject, Issue.ROBUSTNESS );
    }

    private boolean test( QueryService queryService, ModelObject modelObject, String test ) {
        return modelObject instanceof Plan ?
                passes( queryService, (Plan) modelObject, test ) :
                modelObject instanceof Segment ?
                        passes( queryService, (Segment) modelObject, test ) :
                        hasNoTestedIssue( queryService, modelObject, test );
    }

    private boolean passes( QueryService queryService, Plan plan, String test ) {
        if ( !hasNoTestedIssue( queryService, plan, test ) )
            return false;

        for ( Segment segment : plan.getSegments() )
            if ( !passes( queryService, segment, test ) )
                return false;

        for ( ModelEntity entity : queryService.list( ModelEntity.class ) )
            if ( !hasNoTestedIssue( queryService, entity, test ) )
                return false;

        return true;
    }

    private boolean passes( QueryService queryService, Segment segment, String test ) {
        if ( !hasNoTestedIssue( queryService, segment, test ) )
            return false;

        Iterator<Part> parts = segment.parts();
        while ( parts.hasNext() )
            if ( !hasNoTestedIssue( queryService, parts.next(), test ) )
                return false;

        Iterator<Flow> flows = segment.flows();
        while ( flows.hasNext() )
            if ( !hasNoTestedIssue( queryService, flows.next(), test ) )
                return false;
        return true;
    }

    private boolean hasNoTestedIssue( QueryService queryService, ModelObject modelObject, final String test ) {
        return CollectionUtils.select( listUnwaivedIssues( queryService, modelObject, true ), new Predicate() {
            @Override
            public boolean evaluate( Object object ) {
                return ( (Issue) object ).getType().equals( test );
            }
        } ).isEmpty();
    }

    @Override
    public Integer countTestFailures( QueryService queryService, ModelObject modelObject, String test ) {
        return modelObject instanceof Plan ?
                countFailures( queryService, (Plan) modelObject, test ) :
                modelObject instanceof Segment ?
                        countFailures( queryService, (Segment) modelObject, test ) :
                        countTestIssues( queryService, modelObject, test );
    }

    private int countFailures( QueryService queryService, Plan plan, String test ) {
        int count = countTestIssues( queryService, plan, test );
        for ( Segment segment : plan.getSegments() )
            count += countFailures( queryService, segment, test );
        for ( ModelEntity entity : queryService.list( ModelEntity.class ) )
            count += countTestIssues( queryService, entity, test );
        return count;
    }

    private int countFailures( QueryService queryService, Segment segment, String test ) {
        int count = countTestIssues( queryService, segment, test );
        Iterator<Part> parts = segment.parts();
        while ( parts.hasNext() )
            count += countTestIssues( queryService, parts.next(), test );
        Iterator<Flow> flows = segment.flows();
        while ( flows.hasNext() )
            count += countTestIssues( queryService, flows.next(), test );
        return count;
    }

    private int countTestIssues( QueryService queryService, ModelObject modelObject, final String test ) {
        return CollectionUtils.select( listUnwaivedIssues( queryService, modelObject, true ), new Predicate() {
            @Override
            public boolean evaluate( Object object ) {
                return test.equals( ( (Issue) object ).getType() );
            }
        } ).size();
    }

    /**
     * Find the issues on parts and flows for all plays of a resource.
     *
     * @param queryService the query service
     * @param resourceSpec a resource
     * @param specific     whether the match is "equals" or "narrows or equals"
     * @return a list of issues
     */
    private List<Issue> findAllIssuesInPlays( QueryService queryService, ResourceSpec resourceSpec, boolean specific ) {
        List<Issue> issues = new ArrayList<Issue>();
        Set<Part> parts = new HashSet<Part>();
        for ( Play play : queryService.findAllPlays( resourceSpec, specific ) ) {
            parts.add( play.getPartFor( resourceSpec, queryService ) );
            issues.addAll( listIssues( queryService, play.getFlow(), true ) );
        }
        for ( Part part : parts )
            issues.addAll( listIssues( queryService, part, true ) );
        return issues;
    }

    private List<Issue> detectAllIssues( QueryService queryService, ModelObject modelObject, String property,
                                         boolean includingPropertySpecific ) {
        List<Issue> issues = new ArrayList<Issue>();
        if ( property != null ) {
            issues.addAll( detective.detectUnwaivedPropertyIssues( queryService, modelObject, property ) );
            issues.addAll( detective.detectWaivedPropertyIssues( queryService, modelObject, property ) );
        } else {
            if ( includingPropertySpecific ) {
                issues.addAll( detective.detectUnwaivedIssues( queryService, modelObject, true ) );
                issues.addAll( detective.detectWaivedIssues( queryService, modelObject, true ) );
            }
            issues.addAll( detective.detectUnwaivedIssues( queryService, modelObject, false ) );
            issues.addAll( detective.detectWaivedIssues( queryService, modelObject, false ) );
        }
        return issues;
    }

    private List<? extends Issue> detectUnwaivedIssues( QueryService queryService, ModelObject modelObject, String property,
                                                        boolean includingPropertySpecific ) {
        if ( property == null ) {
            List<Issue> issues = new ArrayList<Issue>();
            if ( includingPropertySpecific )
                issues.addAll( detective.detectUnwaivedIssues( queryService, modelObject, true ) );
            issues.addAll( detective.detectUnwaivedIssues( queryService, modelObject, false ) );
            return issues;
        }

        return detective.detectUnwaivedPropertyIssues( queryService, modelObject, property );
    }

    private List<Issue> detectUnwaivedIssues( QueryService queryService, Assignment assignment,
                                              boolean includingPropertySpecific ) {
        List<Issue> issues = new ArrayList<Issue>( detectUnwaivedIssues( queryService,
                assignment.getPart(),
                null,
                includingPropertySpecific ) );

        Actor actor = assignment.getActor();
        if ( actor != null && !actor.isUnknown() )
            issues.addAll( detectUnwaivedIssues( queryService, actor, null, includingPropertySpecific ) );
        Role role = assignment.getRole();
        if ( role != null && !role.isUnknown() )
            issues.addAll( detectUnwaivedIssues( queryService, role, null, includingPropertySpecific ) );
        Organization org = assignment.getOrganization();
        if ( org != null && !org.isUnknown() )
            issues.addAll( detectUnwaivedIssues( queryService, org, null, includingPropertySpecific ) );
        return issues;
    }

    private List<? extends Issue> detectWaivedIssues( QueryService queryService, ModelObject modelObject, String property,
                                                      boolean includingPropertySpecific ) {
        if ( property == null ) {
            List<Issue> issues = new ArrayList<Issue>();
            if ( includingPropertySpecific )
                issues.addAll( detective.detectWaivedIssues( queryService, modelObject, true ) );
            issues.addAll( detective.detectWaivedIssues( queryService, modelObject, false ) );
            return issues;
        }

        return detective.detectWaivedPropertyIssues( queryService, modelObject, property );
    }

    private List<Issue> detectWaivedIssues( QueryService queryService, Assignment assignment,
                                            boolean includingPropertySpecific ) {
        List<Issue> issues = new ArrayList<Issue>( detectWaivedIssues( queryService,
                assignment.getPart(),
                null,
                includingPropertySpecific ) );

        Actor actor = assignment.getActor();
        if ( actor != null && !actor.isUnknown() )
            issues.addAll( detectWaivedIssues( queryService, actor, null, includingPropertySpecific ) );
        Role role = assignment.getRole();
        if ( role != null && !role.isUnknown() )
            issues.addAll( detectWaivedIssues( queryService, role, null, includingPropertySpecific ) );
        Organization org = assignment.getOrganization();
        if ( org != null && !org.isUnknown() )
            issues.addAll( detectWaivedIssues( queryService, org, null, includingPropertySpecific ) );
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
    public List<Issue> findAllIssues( QueryService queryService ) {
        List<Issue> allIssues = new ArrayList<Issue>();
        // allIssues.addAll( analyst.listIssues( getPlan(), true ) );
        for ( ModelObject mo : queryService.list( ModelObject.class ) )
            allIssues.addAll( listIssues( queryService, mo, true ) );
        for ( Segment segment : queryService.list( Segment.class ) ) {
            Iterator<Part> parts = segment.parts();
            while ( parts.hasNext() )
                allIssues.addAll( listIssues( queryService, parts.next(), true ) );
            Iterator<Flow> flows = segment.flows();
            while ( flows.hasNext() )
                allIssues.addAll( listIssues( queryService, flows.next(), true ) );
        }
        return allIssues;
    }

    @Override
    public List<Issue> findAllUnwaivedIssues( QueryService queryService ) {
        List<Issue> allUnwaivedIssues = new ArrayList<Issue>();
        // allUnwaivedIssues.addAll( analyst.listUnwaivedIssues( getPlan(), true ) );
        for ( ModelObject mo : queryService.list( ModelObject.class ) )
            allUnwaivedIssues.addAll( listUnwaivedIssues( queryService, mo, true ) );
        for ( Segment segment : queryService.list( Segment.class ) ) {
            Iterator<Part> parts = segment.parts();
            while ( parts.hasNext() )
                allUnwaivedIssues.addAll( listUnwaivedIssues( queryService, parts.next(), true ) );
            Iterator<Flow> flows = segment.flows();
            while ( flows.hasNext() )
                allUnwaivedIssues.addAll( listUnwaivedIssues( queryService, flows.next(), true ) );
        }
        return allUnwaivedIssues;
    }

    @Override
    public List<Issue> findAllWaivedIssues( QueryService queryService ) {
        List<Issue> allWaivedIssues = new ArrayList<Issue>();
        // allUnwaivedIssues.addAll( analyst.listUnwaivedIssues( getPlan(), true ) );
        for ( ModelObject mo : queryService.list( ModelObject.class ) )
            allWaivedIssues.addAll( listWaivedIssues( queryService, mo, true ) );
        for ( Segment segment : queryService.list( Segment.class ) ) {
            Iterator<Part> parts = segment.parts();
            while ( parts.hasNext() )
                allWaivedIssues.addAll( listWaivedIssues( queryService, parts.next(), true ) );
            Iterator<Flow> flows = segment.flows();
            while ( flows.hasNext() )
                allWaivedIssues.addAll( listWaivedIssues( queryService, flows.next(), true ) );
        }
        return allWaivedIssues;
    }

    @Override
    public SegmentRelationship findSegmentRelationship( QueryService queryService, Segment fromSegment,
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
        for ( Part part : queryService.findInitiators( toSegment ) )
            if ( part.getSegment().equals( fromSegment ) )
                initiators.add( part );
        for ( Part part : queryService.findExternalTerminators( toSegment ) )
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
    public Boolean canBeRealized( Commitment commitment, Plan plan, QueryService queryService ) {
        return findRealizabilityProblems( plan, commitment, queryService ).isEmpty();
    }


    @Override
    public <T extends ModelEntity> EntityRelationship<T> findEntityRelationship( QueryService queryService,
                                                                                 T fromEntity, T toEntity ) {
        return findEntityRelationship( queryService, fromEntity, toEntity, null );
    }

    @Override
    public <T extends ModelEntity> EntityRelationship<T> findEntityRelationship( QueryService queryService,
                                                                                 T fromEntity, T toEntity,
                                                                                 Segment segment ) {
        Place planLocale = queryService.getPlanLocale();
        Commitments commitments = Commitments.all( queryService )
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
    public List<EntityRelationship> findEntityRelationships( QueryService queryService, Segment segment,
                                                             Class<? extends ModelEntity> entityClass, Kind kind ) {
        List<EntityRelationship> rels = new ArrayList<EntityRelationship>();
        List<? extends ModelEntity> entities =
                kind == Kind.Actual
                        ? queryService.listActualEntities( entityClass )
                        : queryService.listTypeEntities( entityClass );
        for ( ModelEntity entity : entities ) {
            if ( !entity.isUnknown() )
                rels.addAll( findEntityRelationships( segment, entity, queryService ) );
        }
        return rels;
    }

    @Override
    public List<EntityRelationship> findEntityRelationships( Segment segment, ModelEntity entity,
                                                             QueryService queryService ) {
        List<EntityRelationship> rels = new ArrayList<EntityRelationship>();
        Place planLocale = queryService.getPlanLocale();
        // Committing relationships
        Commitments entityCommittingCommitments = Commitments.all( queryService )
                .inSegment( segment )
                .withEntityCommitting( entity, planLocale );
        List<? extends ModelEntity> otherEntities =
                entity.isActual()
                        ? queryService.listActualEntities( entity.getClass() )
                        : queryService.listTypeEntities( entity.getClass() );

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
        Commitments entityBenefitingCommitments = Commitments.all( queryService )
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
    public Boolean isEffectivelyConceptual( QueryService queryService, Part part ) {
        return !findConceptualCauses( queryService, part ).isEmpty();
    }

    @Override
    public List<String> findConceptualCauses( QueryService queryService, Part part ) {
        List<String> causes = new ArrayList<String>();
        if ( part.isProhibited() )
            causes.add( "prohibited" );
        List<Assignment> assignments = queryService.findAllAssignments( part, false );
        if ( assignments.isEmpty() )
            causes.add( "no agent is assigned to the task" );
        else if ( noAvailability( assignments ) )
            causes.add( "none of the assigned agents is ever available" );
        return causes;
    }

    @Override
    public List<String> findConceptualRemediations( QueryService queryService, Part part ) {
        List<String> remediations = new ArrayList<String>();
        if ( part.isProhibited() )
            remediations.add( "remove the prohibition" );
        List<Assignment> assignments = queryService.findAllAssignments( part, false );
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
    public Boolean isEffectivelyConceptual( QueryService queryService, Flow flow ) {
        return !flow.isToSelf() && !findConceptualCauses( queryService, flow ).isEmpty();
    }

    @Override
    public List<String> findConceptualCauses( QueryService queryService, Flow flow ) {
        List<String> causes = new ArrayList<String>();
        if ( flow.isProhibited() )
            causes.add( "prohibited" );
        if ( flow.isNeed() && isEffectivelyConceptual( queryService, (Part) flow.getTarget() ) )
            causes.add( "this task can not be executed" );
        else if ( flow.isCapability() && isEffectivelyConceptual( queryService, (Part) flow.getSource() ) )
            causes.add( "this can not be executed" );
        else if ( flow.isSharing() ) {
            if ( isEffectivelyConceptual( queryService, (Part) flow.getSource() ) )
                causes.add( "the task \"" + ( (Part) flow.getSource() ).getTask() + "\" can not be executed" );
            if ( isEffectivelyConceptual( queryService, (Part) flow.getTarget() ) )
                causes.add( "the task \"" + ( (Part) flow.getTarget() ).getTask() + "\" can not be executed" );
            if ( flow.getEffectiveChannels().isEmpty() )
                causes.add( "no channels is identified" );
            else {
                List<Commitment> commitments =
                        queryService.findAllCommitments( flow, false, queryService.getAssignments( false ) );
                if ( commitments.isEmpty() ) {
                    causes.add( "there are no communication commitments between any pair of agents" );
                } else {
                    StringBuilder sb = new StringBuilder();
                    Plan plan = queryService.getPlan();
                    Place locale = queryService.getPlanLocale();
                    List<Commitment> agreedTo = agreedToFilter( commitments, queryService );
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
    public List<String> findConceptualRemediations( QueryService queryService, Flow flow ) {
        List<String> remediations = new ArrayList<String>();
        if ( flow.isProhibited() )
            remediations.add( "remove the prohibition" );
        if ( flow.isNeed() && isEffectivelyConceptual( queryService, (Part) flow.getTarget() ) )
            remediations.add( "make this task executable" );
        else if ( flow.isCapability() && isEffectivelyConceptual( queryService, (Part) flow.getSource() ) )
            remediations.add( "make this task executable" );
        else if ( flow.isSharing() ) {
            if ( isEffectivelyConceptual( queryService, (Part) flow.getSource() ) )
                remediations.add( "make the task \"" + ( (Part) flow.getSource() ).getTask() + "\" executable" );
            if ( isEffectivelyConceptual( queryService, (Part) flow.getTarget() ) )
                remediations.add( "make the task \"" + ( (Part) flow.getTarget() ).getTask() + "\" executable" );
            if ( flow.getEffectiveChannels().isEmpty() )
                remediations.add( "add at least one channel to the flow" );
            else {
                List<Commitment> commitments = queryService.findAllCommitments( flow );
                if ( commitments.isEmpty() ) {
                    remediations.add(
                            "change the definitions of the source and/or target tasks so that agents are assigned to both" );
                    remediations.add(
                            "add jobs to relevant organizations so that agents can be assigned to source and/or target tasks" );
                } else {
                    Plan plan = queryService.getPlan();
                    Place locale = queryService.getPlanLocale();
                    List<Commitment> agreedTo = agreedToFilter( commitments, queryService );
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
    @SuppressWarnings("unchecked")
    private List<Commitment> agreedToFilter( List<Commitment> commitments, final QueryService queryService ) {
        return (List<Commitment>) CollectionUtils.select( commitments, new Predicate() {
            @Override
            public boolean evaluate( Object object ) {
                return queryService.isAgreedToIfRequired( (Commitment) object );
            }
        } );
    }

    // Filter commitments where agent availabilities coincide if required.
    @SuppressWarnings("unchecked")
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
    @SuppressWarnings("unchecked")
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
    @SuppressWarnings("unchecked")
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
    @SuppressWarnings("unchecked")
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
        List<String> problems = findRealizabilityProblems( planService.getPlan(), commitment, planService );
        return problems.isEmpty() ?
                "Yes" :
                "No: " + StringUtils.capitalize( ChannelsUtils.listToString( problems, ", and " ) );
    }

    @Override
    public List<String> findRealizabilityProblems( Plan plan, Commitment commitment, QueryService queryService ) {
        List<String> problems = new ArrayList<String>();
        List<TransmissionMedium> mediaUsed = commitment.getSharing().transmissionMedia();
        Place planLocale = queryService.getPlanLocale();
/*
        if ( !queryService.isAgreedToIfRequired( commitment ) )
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
    public int unwaivedIssuesCount( Requirement requirement, QueryService queryService ) {
        return detectUnwaivedIssues( queryService, requirement, null, true ).size();
    }

    @Override
    public int unwaivedIssuesCount( Requirement requirement, CommunityService communityService ) {
        return unwaivedIssuesCount( requirement, communityService.getPlanService() );
    }

    @Override
    public int allIssuesCount( Requirement requirement, QueryService queryService ) {
        return detectAllIssues( queryService, requirement, null, true ).size();
    }

    @Override
    public void commandDone( Commander commander, Command command, Change change ) {
        if ( !commander.isReplaying() && command.isTop() && !change.isNone() )
            onAfterCommand( commander.getPlan() );
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
        onStart( commander.getPlan() );
    }
}
