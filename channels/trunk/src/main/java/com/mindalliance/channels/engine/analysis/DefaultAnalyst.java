package com.mindalliance.channels.engine.analysis;

import com.mindalliance.channels.core.dao.User;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Assignment;
import com.mindalliance.channels.core.model.Commitment;
import com.mindalliance.channels.core.model.ExternalFlow;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.model.ResourceSpec;
import com.mindalliance.channels.core.model.Role;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.model.TransmissionMedium;
import com.mindalliance.channels.core.util.ChannelsUtils;
import com.mindalliance.channels.engine.analysis.graph.EntityRelationship;
import com.mindalliance.channels.engine.analysis.graph.SegmentRelationship;
import com.mindalliance.channels.engine.imaging.ImagingService;
import com.mindalliance.channels.engine.query.Play;
import com.mindalliance.channels.engine.query.QueryService;
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
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 26, 2008
 * Time: 10:07:27 AM
 */
public class DefaultAnalyst implements Analyst, Lifecycle {

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

    private ImagingService imagingService;

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

    /**
     * Enable analysis of problems in active plans.
     */
    public void start() {
        running = true;
        // onStart();
    }

    /**
     * Disable analysis of problems in active plans.
     */
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
    public void onStart( Plan plan ) {
        issueScanner.scan( plan );
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

    /**
     * {@inheritDoc}
     */
    public List<Issue> listUnwaivedIssues(
            ModelObject modelObject, Boolean includingPropertySpecific ) {
        return detectUnwaivedIssues(
                modelObject,
                null,
                includingPropertySpecific );
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> listUnwaivedIssues( ModelObject modelObject, String property ) {
        return detectUnwaivedIssues( modelObject, property, true );
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> listUnwaivedIssues( Assignment assignment, Boolean includingPropertySpecific ) {
        return detectUnwaivedIssues( assignment, includingPropertySpecific );
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> listWaivedIssues(
            ModelObject modelObject, Boolean includingPropertySpecific ) {
        return detectWaivedIssues(
                modelObject,
                null,
                includingPropertySpecific );
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> listWaivedIssues( ModelObject modelObject, String property ) {
        return detectWaivedIssues( modelObject, property, true );
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> listWaivedIssues( Assignment assignment, Boolean includingPropertySpecific ) {
        return detectWaivedIssues( assignment, includingPropertySpecific );
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
    public Boolean hasUnwaivedIssues( Assignment assignment, Boolean includingPropertySpecific ) {
        return !listUnwaivedIssues( assignment, includingPropertySpecific ).isEmpty();
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
    public String getIssuesSummary( Assignment assignment, Boolean includingPropertySpecific ) {
        List<Issue> issues = listUnwaivedIssues( assignment, includingPropertySpecific );
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
            return modelObject instanceof Segment ? passes( (Segment) modelObject, test )
                    : hasNoTestedIssue( modelObject, test );
    }

    private boolean passes( Plan plan, String test ) {
        if ( !hasNoTestedIssue( plan, test ) )
            return false;
        for ( Segment segment : plan.getSegments() ) {
            if ( !passes( segment, test ) )
                return false;
        }
        for ( ModelEntity entity : queryService.list( ModelEntity.class ) ) {
            if ( !hasNoTestedIssue( entity, test ) )
                return false;
        }
        return true;
    }

    private boolean passes( Segment segment, String test ) {
        if ( !hasNoTestedIssue( segment, test ) )
            return false;
        Iterator<Part> parts = segment.parts();
        while ( parts.hasNext() ) {
            if ( !hasNoTestedIssue( parts.next(), test ) )
                return false;
        }
        Iterator<Flow> flows = segment.flows();
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
        } else if ( modelObject instanceof Segment ) {
            return countFailures( (Segment) modelObject, test );
        } else {
            return countTestIssues( modelObject, test );
        }
    }

    private int countFailures( Plan plan, String test ) {
        int count = countTestIssues( plan, test );
        for ( Segment segment : plan.getSegments() ) {
            count += countFailures( segment, test );
        }
        for ( ModelEntity entity : queryService.list( ModelEntity.class ) ) {
            count += countTestIssues( entity, test );
        }
        return count;
    }

    private int countFailures( Segment segment, String test ) {
        int count = countTestIssues( segment, test );
        Iterator<Part> parts = segment.parts();
        while ( parts.hasNext() )
            count += countTestIssues( parts.next(), test );
        Iterator<Flow> flows = segment.flows();
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

    private List<Issue> detectUnwaivedIssues(
            Assignment assignment,
            boolean includingPropertySpecific ) {
        List<Issue> issues = new ArrayList<Issue>();
        issues.addAll( detectUnwaivedIssues( assignment.getPart(), null, includingPropertySpecific ) );
        Actor actor = assignment.getActor();
        if ( actor != null && !actor.isUnknown() ) {
            issues.addAll( detectUnwaivedIssues( actor, null, includingPropertySpecific ) );
        }
        Role role = assignment.getRole();
        if ( role != null && !role.isUnknown() ) {
            issues.addAll( detectUnwaivedIssues( role, null, includingPropertySpecific ) );
        }
        Organization org = assignment.getOrganization();
        if ( org != null && !org.isUnknown() ) {
            issues.addAll( detectUnwaivedIssues( org, null, includingPropertySpecific ) );
        }
        return issues;
    }

    private List<Issue> detectWaivedIssues(
            ModelObject modelObject,
            String property,
            boolean includingPropertySpecific ) {
        if ( property != null ) {
            return detective.detectWaivedPropertyIssues( modelObject, property );
        } else {
            List<Issue> issues = new ArrayList<Issue>();
            if ( includingPropertySpecific ) {
                issues.addAll( detective.detectWaivedIssues( modelObject, true ) );
            }
            issues.addAll( detective.detectWaivedIssues( modelObject, false ) );
            return issues;
        }
    }

    private List<Issue> detectWaivedIssues(
            Assignment assignment,
            boolean includingPropertySpecific ) {
        List<Issue> issues = new ArrayList<Issue>();
        issues.addAll( detectWaivedIssues( assignment.getPart(), null, includingPropertySpecific ) );
        Actor actor = assignment.getActor();
        if ( actor != null && !actor.isUnknown() ) {
            issues.addAll( detectWaivedIssues( actor, null, includingPropertySpecific ) );
        }
        Role role = assignment.getRole();
        if ( role != null && !role.isUnknown() ) {
            issues.addAll( detectWaivedIssues( role, null, includingPropertySpecific ) );
        }
        Organization org = assignment.getOrganization();
        if ( org != null && !org.isUnknown() ) {
            issues.addAll( detectWaivedIssues( org, null, includingPropertySpecific ) );
        }
        return issues;
    }


    /**
     * Get the imaging service.
     *
     * @return the imaging service
     */
    public ImagingService getImagingService() {
        return imagingService;
    }

    public void setImagingService( ImagingService imagingService ) {
        this.imagingService = imagingService;
    }

    public List<Issue> findAllIssues() {
        List<Issue> allIssues = new ArrayList<Issue>();
        // allIssues.addAll( analyst.listIssues( getPlan(), true ) );
        for ( ModelObject mo : queryService.list( ModelObject.class ) ) {
            allIssues.addAll( listIssues( mo, true ) );
        }
        for ( Segment segment : queryService.list( Segment.class ) ) {
            Iterator<Part> parts = segment.parts();
            while ( parts.hasNext() ) {
                allIssues.addAll( listIssues( parts.next(), true ) );
            }
            Iterator<Flow> flows = segment.flows();
            while ( flows.hasNext() ) {
                allIssues.addAll( listIssues( flows.next(), true ) );
            }
        }
        return allIssues;
    }

    /**
     * Find all unwaived issues on all model objects in the plan.
     *
     * @return a list of issues.
     */
    public List<Issue> findAllUnwaivedIssues() {
        List<Issue> allUnwaivedIssues = new ArrayList<Issue>();
        // allUnwaivedIssues.addAll( analyst.listUnwaivedIssues( getPlan(), true ) );
        for ( ModelObject mo : queryService.list( ModelObject.class ) ) {
            allUnwaivedIssues.addAll( listUnwaivedIssues( mo, true ) );
        }
        for ( Segment segment : queryService.list( Segment.class ) ) {
            Iterator<Part> parts = segment.parts();
            while ( parts.hasNext() ) {
                allUnwaivedIssues.addAll( listUnwaivedIssues( parts.next(), true ) );
            }
            Iterator<Flow> flows = segment.flows();
            while ( flows.hasNext() ) {
                allUnwaivedIssues.addAll( listUnwaivedIssues( flows.next(), true ) );
            }
        }
        return allUnwaivedIssues;
    }

    @Override
    public List<Issue> findAllWaivedIssues() {
        List<Issue> allWaivedIssues = new ArrayList<Issue>();
        // allUnwaivedIssues.addAll( analyst.listUnwaivedIssues( getPlan(), true ) );
        for ( ModelObject mo : queryService.list( ModelObject.class ) ) {
            allWaivedIssues.addAll( listWaivedIssues( mo, true ) );
        }
        for ( Segment segment : queryService.list( Segment.class ) ) {
            Iterator<Part> parts = segment.parts();
            while ( parts.hasNext() ) {
                allWaivedIssues.addAll( listWaivedIssues( parts.next(), true ) );
            }
            Iterator<Flow> flows = segment.flows();
            while ( flows.hasNext() ) {
                allWaivedIssues.addAll( listWaivedIssues( flows.next(), true ) );
            }
        }
        return allWaivedIssues;
    }

    /**
     * {@inheritDoc}
     */
    public SegmentRelationship findSegmentRelationship( Segment fromSegment, Segment toSegment ) {
        List<ExternalFlow> externalFlows = new ArrayList<ExternalFlow>();
        List<Part> initiators = new ArrayList<Part>();
        List<Part> terminators = new ArrayList<Part>();
        Iterator<Flow> flows = fromSegment.flows();
        while ( flows.hasNext() ) {
            Flow flow = flows.next();
            if ( flow.isExternal() ) {
                ExternalFlow externalFlow = (ExternalFlow) flow;
                if ( externalFlow.getConnector().getSegment() == toSegment
                        && !externalFlow.isPartTargeted() ) {
                    externalFlows.add( externalFlow );
                }
            }
        }
        flows = toSegment.flows();
        while ( flows.hasNext() ) {
            Flow flow = flows.next();
            if ( flow.isExternal() ) {
                ExternalFlow externalFlow = (ExternalFlow) flow;
                if ( externalFlow.getConnector().getSegment() == fromSegment
                        && externalFlow.isPartTargeted() ) {
                    externalFlows.add( externalFlow );
                }
            }
        }
        for ( Part part : queryService.findInitiators( toSegment ) ) {
            if ( part.getSegment().equals( fromSegment ) ) initiators.add( part );
        }
        for ( Part part : queryService.findExternalTerminators( toSegment ) ) {
            if ( part.getSegment().equals( fromSegment ) ) terminators.add( part );
        }
        if ( externalFlows.isEmpty() && initiators.isEmpty() && terminators.isEmpty() ) {
            return null;
        } else {
            SegmentRelationship segmentRelationship = new SegmentRelationship(
                    fromSegment,
                    toSegment );
            segmentRelationship.setExternalFlows( externalFlows );
            segmentRelationship.setInitiators( initiators );
            segmentRelationship.setTerminators( terminators );
            return segmentRelationship;
        }
    }

    /**
     * {@inheritDoc}
     */
    public <T extends ModelEntity> EntityRelationship<T> findEntityRelationship(
            T fromEntity, T toEntity ) {
        return findEntityRelationship( fromEntity, toEntity, null );
    }

    /**
     * {@inheritDoc}
     */
    public <T extends ModelEntity> EntityRelationship<T> findEntityRelationship(
            T fromEntity, T toEntity, Segment segment ) {
        List<Flow> entityFlows = new ArrayList<Flow>();
        List<Segment> segments = new ArrayList<Segment>();
        if ( segment == null ) {
            segments.addAll( queryService.list( Segment.class ) );
        } else {
            segments.add( segment );
        }
        for ( Segment seg : segments ) {
            for ( Flow flow : seg.getAllSharingFlows() ) {
                Part sourcePart = (Part) flow.getSource();
                Part targetPart = (Part) flow.getTarget();
                if ( queryService.isExecutedBy( sourcePart, fromEntity )
                        && queryService.isExecutedBy( targetPart, toEntity ) ) {
                    entityFlows.add( flow );
                }
            }
        }
        if ( entityFlows.isEmpty() ) {
            return null;
        } else {
            EntityRelationship<T> entityRel = new EntityRelationship<T>( fromEntity, toEntity );
            entityRel.setFlows( entityFlows );
            return entityRel;
        }
    }

    /**
     * {@inheritDoc}
     */
    public List<EntityRelationship> findEntityRelationships(
            Segment segment, Class<? extends ModelEntity> entityClass, ModelEntity.Kind kind ) {
        List<ModelEntity> entities = queryService.findTaskedEntities( segment, entityClass, kind );
        List<EntityRelationship> rels = new ArrayList<EntityRelationship>();
        for ( ModelEntity entity : entities ) {
            for ( ModelEntity otherEntity : entities ) {
                if ( !entity.equals( otherEntity ) ) {
                    EntityRelationship<ModelEntity> sendRel =
                            findEntityRelationship( entity, otherEntity, segment );
                    if ( sendRel != null ) {
                        rels.add( sendRel );
                    }
                }
            }
        }
        return rels;
    }

    /**
     * {@inheritDoc}
     */
    public List<EntityRelationship> findEntityRelationships( Segment segment, ModelEntity entity ) {
        List<ModelEntity> otherEntities =
                new ArrayList<ModelEntity>(
                        queryService.findTaskedEntities( segment, entity.getClass(), entity.getKind() ) );
        otherEntities.remove( entity );
        List<EntityRelationship> rels = new ArrayList<EntityRelationship>();
        for ( ModelEntity otherEntity : otherEntities ) {
            EntityRelationship<ModelEntity> sendRel =
                    findEntityRelationship( entity, otherEntity );
            if ( sendRel != null ) {
                rels.add( sendRel );
            }
            EntityRelationship<ModelEntity> receiveRel =
                    findEntityRelationship( otherEntity, entity );
            if ( receiveRel != null ) {
                rels.add( receiveRel );
            }
        }
        return rels;
    }

    @Override
    public Boolean isEffectivelyConceptual( Part part ) {
        return !findConceptualCauses( part ).isEmpty();
    }

    @Override
    public List<String> findConceptualCauses( Part part ) {
        List<String> causes = new ArrayList<String>();
        if ( part.isProhibited() ) {
            causes.add( "prohibited" );
        }
        if ( part.isDeFactoConceptual() ) {
            StringBuilder sb = new StringBuilder();
            sb.append( "de facto" );
            if ( !part.getConceptualReason().isEmpty() ) {
                sb.append( " because " );
                sb.append( ChannelsUtils.smartUncapitalize( part.getConceptualReason() ) );
            }
            causes.add( sb.toString() );
        } else {
            List<Assignment> assignments = getQueryService().findAllAssignments( part, false );
            if ( assignments.isEmpty() ) {
                causes.add( "no agent is assigned to the task" );
            } else if ( noAvailability( assignments ) ) {
                causes.add( "none of the assigned agents is ever available" );
            }
        }
        return causes;
    }

    @Override
    public List<String> findConceptualRemediations( Part part ) {
        List<String> remediations = new ArrayList<String>();
        if ( part.isProhibited() ) {
            remediations.add( "remove the prohibition" );
        }
        if ( part.isDeFactoConceptual() ) {
            remediations.add( "un-mark the task as de facto conceptual" );
        } else {
            List<Assignment> assignments = getQueryService().findAllAssignments( part, false );
            if ( assignments.isEmpty() ) {
                remediations.add( "explicitly assign an agent to the task" );
                remediations.add( "profile an agent to match the task specifications" );
                remediations.add( "modify the task specifications so that it matches at least one agent" );
            } else if ( noAvailability( assignments ) ) {
                remediations.add( "make assigned agents available" );
            }
        }
        return remediations;
    }


    @Override
    public Boolean isEffectivelyConceptual( Flow flow ) {
        return !findConceptualCauses( flow ).isEmpty();
    }

    @Override
    public List<String> findConceptualCauses( Flow flow ) {
        List<String> causes = new ArrayList<String>();
        if ( flow.isProhibited() ) {
            causes.add( "prohibited" );
        }
        if ( flow.isDeFactoConceptual() ) {
            StringBuilder sb = new StringBuilder();
            sb.append( "de facto" );
            if ( !flow.getConceptualReason().isEmpty() ) {
                sb.append( " because " );
                sb.append( ChannelsUtils.smartUncapitalize( flow.getConceptualReason() ) );
            }
            causes.add( sb.toString() );
        } else {
            if ( flow.isNeed() && isEffectivelyConceptual( (Part) flow.getTarget() ) ) {
                causes.add( "this task is conceptual" );
            } else if ( flow.isCapability() && isEffectivelyConceptual( (Part) flow.getSource() ) ) {
                causes.add( "this task is conceptual" );
            } else if ( flow.isSharing() ) {
                if ( isEffectivelyConceptual( (Part) flow.getSource() ) ) {
                    causes.add( "the task \""
                            + ( (Part) flow.getSource() ).getTask()
                            + "\" is conceptual" );
                }
                if ( isEffectivelyConceptual( (Part) flow.getTarget() ) ) {
                    causes.add( "the task \""
                            + ( (Part) flow.getTarget() ).getTask()
                            + "\" is conceptual" );
                }
                if ( flow.getEffectiveChannels().isEmpty() ) {
                    causes.add( "no channels is identified" );
                } else {
                    List<Commitment> commitments = getQueryService().findAllCommitments( flow );
                    if ( commitments.isEmpty() ) {
                        causes.add( "there are no sharing commitments between any pair of agents" );
                    } else {
                        StringBuilder sb = new StringBuilder();
                        List<Commitment> availabilityOverlaps = availabilityOverlapsFilter( commitments );
                        if ( availabilityOverlaps.isEmpty() ) {
                            sb.append( "in all sharing commitments, " );
                            sb.append( "agents are never available at the same time" );
                        }
                        List<TransmissionMedium> mediaUsed = flow.transmissionMedia();
                        List<Commitment> mediaDeployed = mediaDeployedFilter( availabilityOverlaps, mediaUsed );
                        if ( mediaDeployed.isEmpty() ) {
                            if ( sb.length() == 0 )
                                sb.append( "in all sharing commitments, " );
                            else
                                sb.append( ", or " );
                            sb.append( "agents do not have access to required transmission media" );
                        }
                        List<Commitment> reachable = reachableFilter( availabilityOverlaps, mediaUsed );
                        if ( reachable.isEmpty() ) {
                            if ( sb.length() == 0 )
                                sb.append( "in all sharing commitments, " );
                            else
                                sb.append( ", or " );
                            sb.append( "the agent to be contacted is not reachable (no contact info)" );
                        }
                        List<Commitment> agentsQualified = agentsQualifiedFilter( reachable, mediaUsed );
                        if ( agentsQualified.isEmpty() ) {
                            if ( sb.length() == 0 )
                                sb.append( "in all sharing commitments, " );
                            else
                                sb.append( ", or " );
                            sb.append( "both agents are not qualified to use a transmission medium" );
                        }
                        List<Commitment> languageOverlap = commonLanguageFilter( agentsQualified );
                        if ( languageOverlap.isEmpty() ) {
                            if ( sb.length() == 0 )
                                sb.append( "in all sharing commitments, " );
                            else
                                sb.append( ", or " );
                            sb.append( "agents do not speak a common language" );
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
    public List<String> findConceptualRemediations
            ( Flow
                      flow ) {
        List<String> remediations = new ArrayList<String>();
        if ( flow.isProhibited() ) {
            remediations.add( "remove the prohibition" );
        }
        if ( flow.isDeFactoConceptual() ) {
            remediations.add( "un-mark this flow as de facto conceptual" );
        } else {
            if ( flow.isNeed() && isEffectivelyConceptual( (Part) flow.getTarget() ) ) {
                remediations.add( "make this task not conceptual" );
            } else if ( flow.isCapability() && isEffectivelyConceptual( (Part) flow.getSource() ) ) {
                remediations.add( "make this task not conceptual" );
            } else if ( flow.isSharing() ) {
                if ( isEffectivelyConceptual( (Part) flow.getSource() ) ) {
                    remediations.add( "make the task \""
                            + ( (Part) flow.getSource() ).getTask()
                            + "\" not conceptual" );
                }
                if ( isEffectivelyConceptual( (Part) flow.getTarget() ) ) {
                    remediations.add( "make the task \""
                            + ( (Part) flow.getTarget() ).getTask()
                            + "\" not conceptual" );
                }
                if ( flow.getEffectiveChannels().isEmpty() ) {
                    remediations.add( "add at least one channel to the flow" );
                } else {
                    List<Commitment> commitments = getQueryService().findAllCommitments( flow );
                    if ( commitments.isEmpty() ) {
                        remediations.add( "change the definitions of the source and/or target tasks so that agents are assigned to both" );
                        remediations.add( "add jobs to relevant organizations so that agents can be assigned to source and/or target tasks" );
                    }
                    List<Commitment> availabilityOverlaps = availabilityOverlapsFilter( commitments );
                    if ( availabilityOverlaps.isEmpty() ) {
                        remediations.add( "change agent availability to make them coincide" );
                    }
                    List<TransmissionMedium> mediaUsed = flow.transmissionMedia();
                    List<Commitment> mediaDeployed = mediaDeployedFilter( availabilityOverlaps, mediaUsed );
                    if ( mediaDeployed.isEmpty() ) {
                        remediations.add( "make sure that the agents that are available" +
                                " to each other also have access to required transmission media" );
                    }
                    List<Commitment> reachable = reachableFilter( availabilityOverlaps, mediaUsed );
                    if ( reachable.isEmpty() ) {
                        remediations.add( "make sure that the agents that are available" +
                                " to each other also have known contact information" );
                    }
                    List<Commitment> agentsQualified = agentsQualifiedFilter( reachable, mediaUsed );
                    if ( agentsQualified.isEmpty() ) {
                        remediations.add( "make sure that agents that are available to each other" +
                                " and reachable are also qualified to use the transmission media" );
                        remediations.add( "add channels with transmission media requiring no qualification" );
                    }
                    if ( commonLanguageFilter( commitments ).isEmpty() ) {
                        remediations.add( "make sure that agents that are available to each other, " +
                                "reachable and qualified to use the transmission media " +
                                "can also speak a common language" );
                    }
                }
            }
        }
        return remediations;
    }

    // Filter commitments where agent availabilities overlap.
    @SuppressWarnings( "unchecked" )
    private List<Commitment> availabilityOverlapsFilter
    (
            final List<Commitment> commitments ) {
        return (List<Commitment>) CollectionUtils.select(
                commitments,
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return isAvailabilityOverlaps( (Commitment) object );
                    }
                }
        );
    }

    public boolean isAvailabilityOverlaps( Commitment commitment ) {
        Actor committer = commitment.getCommitter().getActor();
        Actor beneficiary = commitment.getBeneficiary().getActor();
        return !committer.getAvailability()
                .overlap( beneficiary.getAvailability() ).isEmpty();
    }


    // Filter commitments where agent have access to required transmission media.
    @SuppressWarnings( "unchecked" )
    private List<Commitment> mediaDeployedFilter
    ( List<Commitment> commitments, final List<TransmissionMedium> mediaUsed ) {
        final Place planLocale = planLocale();
        return (List<Commitment>) CollectionUtils.select(
                commitments,
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        final Commitment commitment = (Commitment) object;
                        return isMediaDeployed( commitment, mediaUsed, planLocale );
                    }
                }
        );
    }

    public boolean isMediaDeployed( final Commitment commitment,
                                    final List<TransmissionMedium> mediaUsed,
                                    final Place planLocale ) {
        return CollectionUtils.exists(
                mediaUsed,
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        TransmissionMedium medium = (TransmissionMedium) object;
                        return commitment.getCommitter().getOrganization().isMediumDeployed( medium, planLocale )
                                && commitment.getBeneficiary().getOrganization().isMediumDeployed( medium, planLocale );
                    }
                }
        );
    }

    // Filter commitments where agent to eb contacted has known contact info.
    @SuppressWarnings( "unchecked" )
    private List<Commitment> reachableFilter
    ( List<Commitment> commitments, final List<TransmissionMedium> mediaUsed ) {
        final Place planLocale = planLocale();
        return (List<Commitment>) CollectionUtils.select(
                commitments,
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return isReachable( (Commitment) object, mediaUsed, planLocale );
                    }
                }
        );
    }

    public boolean isReachable( final Commitment commitment,
                                final List<TransmissionMedium> mediaUsed,
                                final Place planLocale ) {
        boolean isRequest = commitment.getSharing().isAskedFor();
        final Actor receiver = isRequest
                ? commitment.getCommitter().getActor()
                : commitment.getBeneficiary().getActor();
        return CollectionUtils.exists(
                mediaUsed,
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        TransmissionMedium medium = (TransmissionMedium) object;
                        return !medium.requiresAddress()
                                || !medium.isUnicast()
                                || receiver.hasChannelFor( medium, planLocale );
                    }
                }
        );
    }

    // Filter commitments where both agents are qualified to use a transmission medium.
    @SuppressWarnings( "unchecked" )
    private List<Commitment> agentsQualifiedFilter
    ( List<Commitment> commitments,
      final List<TransmissionMedium> mediaUsed ) {
        final Place planLocale = planLocale();
        return (List<Commitment>) CollectionUtils.select(
                commitments,
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return isAgentsQualified( (Commitment) object, mediaUsed, planLocale );
                    }
                }
        );
    }

    public boolean isAgentsQualified( final Commitment commitment,
                                      final List<TransmissionMedium> mediaUsed,
                                      final Place planLocale ) {
        return CollectionUtils.exists(
                mediaUsed,
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        TransmissionMedium medium = (TransmissionMedium) object;
                        return medium.getQualification() == null
                                || commitment.getCommitter().getActor()
                                .narrowsOrEquals( medium.getQualification(), planLocale )
                                && commitment.getBeneficiary()
                                .getActor().narrowsOrEquals( medium.getQualification(), planLocale );
                    }
                }
        );
    }

    // Filter commitments where agents can understand one another.
    @SuppressWarnings( "unchecked" )
    private List<Commitment> commonLanguageFilter
    ( List<Commitment> commitments ) {
        return (List<Commitment>) CollectionUtils.select(
                commitments,
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return isCommonLanguage( (Commitment) object );
                    }
                }
        );
    }

    public boolean isCommonLanguage( Commitment commitment ) {
        final Plan plan = getPlan();
        Actor committer = commitment.getCommitter().getActor();
        Actor beneficiary = commitment.getBeneficiary().getActor();
        return committer.canSpeakWith( beneficiary, plan );
    }

    public String realizability( Commitment commitment ) {
        List<TransmissionMedium> mediaUsed = commitment.getSharing().transmissionMedia();
        Place planLocale = getPlan().getLocale();
        List<String> problems = new ArrayList<String>(  );
        if ( !isAvailabilityOverlaps( commitment ) ) {
            problems.add( "availabilities do not overlap" );
        }
        if ( !isMediaDeployed( commitment, mediaUsed, planLocale ) ) {
            problems.add( "no access to required transmission media" );
        }
        if ( !isAgentsQualified( commitment, mediaUsed, planLocale )){
            problems.add( "insufficient technical qualification" );
        }
        if ( !isReachable( commitment, mediaUsed, planLocale )) {
            problems.add( "missing contact info" );
        }
        if ( !isCommonLanguage( commitment )) {
            problems.add( "no common language" );
        }
        return problems.size() ==0
                ? "Yes"
                : "No: " + StringUtils.capitalize( ChannelsUtils.listToString( problems, ", and " ) );
    }

    private Plan getPlan() {
        return User.current().getPlan();
    }


    // There is no assigned agent available at any time.
    private boolean noAvailability( final List<Assignment> assignments ) {
        boolean noAvailability = !CollectionUtils.exists(
                assignments,
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        Assignment assignment = (Assignment) object;
                        return !assignment.getActor().getAvailability().isEmpty();
                    }
                }
        );
        return !assignments.isEmpty() && noAvailability;
    }


    private Place planLocale() {
        return User.current().getPlan().getLocale();
    }


}
