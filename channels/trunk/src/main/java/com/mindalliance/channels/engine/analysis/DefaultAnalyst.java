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
import com.mindalliance.channels.core.model.CollaborationModel;
import com.mindalliance.channels.core.model.Commitment;
import com.mindalliance.channels.core.model.ExternalFlow;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.ModelEntity.Kind;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.model.TransmissionMedium;
import com.mindalliance.channels.core.query.Assignments;
import com.mindalliance.channels.core.query.Commitments;
import com.mindalliance.channels.core.query.ModelService;
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


    private ImagingService imagingService;

    private Doctor doctor;

    /**
     * Lifecycle status.
     */
    private boolean running;

    public DefaultAnalyst() {
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor( Doctor doctor ) {
        this.doctor = doctor;
    }

    /**
     * Enable analysis of problems in active plans.
     */
    @Override
    public void start() {
        running = true;
        // onStart();
    }

    private IssueScanner getIssueScanner() {
        return getDoctor().getIssueScanner();
    }

    /**
     * Disable analysis of problems in active plans.
     */
    @Override
    public void stop() {
        getIssueScanner().terminate();
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public void onAfterCommand( PlanCommunity planCommunity ) {
        getIssueScanner().rescan( planCommunity );
    }

    @Override
    public void onStart( PlanCommunity planCommunity ) {
        getIssueScanner().scan( planCommunity );
    }

    @Override
    public void onStop() {
        stop();
    }

    @Override
    public void onDestroy() {
        getIssueScanner().terminate();
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

    public void setDoctor( CollaborationTemplateDoctor doctor ) {
        this.doctor = doctor;
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
        for ( Part part : communityService.getModelService().findInitiators( toSegment ) )
            if ( part.getSegment().equals( fromSegment ) )
                initiators.add( part );
        for ( Part part : communityService.getModelService().findExternalTerminators( toSegment ) )
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
    public Boolean canBeRealized( Commitment commitment, CollaborationModel collaborationModel, CommunityService communityService ) {
        return findRealizabilityProblems( collaborationModel, commitment, communityService ).isEmpty();
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
        Place planLocale = communityService.getModelService().getPlanLocale();
        ModelService modelService = communityService.getModelService();
        Assignments assignments = modelService.getAssignments();
        Commitments commitments = Commitments.all( modelService, assignments )
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
        ModelService modelService = communityService.getModelService();
        List<EntityRelationship> rels = new ArrayList<EntityRelationship>();
        List<? extends ModelEntity> entities =
                kind == Kind.Actual
                        ? modelService.listActualEntities( entityClass )
                        : modelService.listTypeEntities( entityClass );
        for ( ModelEntity entity : entities ) {
            if ( !entity.isUnknown() )
                rels.addAll( findEntityRelationshipsInPlan( segment, entity, communityService ) );
        }
        return rels;
    }

    @Override
    public List<EntityRelationship> findEntityRelationshipsInPlan( Segment segment, ModelEntity entity,
                                                                   CommunityService communityService ) {
        ModelService modelService = communityService.getModelService();
        List<EntityRelationship> rels = new ArrayList<EntityRelationship>();
        Place planLocale = modelService.getPlanLocale();
        // Committing relationships
        Assignments assignments = modelService.getAssignments();
        Commitments entityCommittingCommitments = Commitments.all( modelService, assignments )
                .inSegment( segment )
                .withEntityCommitting( entity, planLocale );
        List<? extends ModelEntity> otherEntities =
                entity.isActual()
                        ? modelService.listActualEntities( entity.getClass() )
                        : modelService.listTypeEntities( entity.getClass() );

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
        Commitments entityBenefitingCommitments = Commitments.all( modelService, assignments )
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
        List<Assignment> assignments = communityService.getModelService().findAllAssignments( part, false );
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
        List<Assignment> assignments = communityService.getModelService().findAllAssignments( part, false );
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
        ModelService modelService = communityService.getModelService();
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
                        modelService.findAllCommitments( flow, false, modelService.getAssignments( false ) );
                if ( commitments.isEmpty() ) {
                    causes.add( "there are no communication commitments between any pair of agents" );
                } else {
                    StringBuilder sb = new StringBuilder();
                    CollaborationModel collaborationModel = communityService.getPlan();
                    Place locale = modelService.getPlanLocale();
                    List<Commitment> agreedTo = agreedToFilter( commitments, communityService.getModelService() );
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
                                sb.append( "the agent to be contacted is not reachable (no channels)" );
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
                                    List<Commitment> languageOverlap = commonLanguageFilter( collaborationModel, agentsQualified );
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
        ModelService modelService = communityService.getModelService();
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
                Assignments allAssignments = modelService.getAssignments();
                List<Commitment> commitments = modelService.findAllCommitments( flow, allAssignments );
                if ( commitments.isEmpty() ) {
                    remediations.add(
                            "change the definitions of the source and/or target tasks so that agents are assigned to both" );
                    remediations.add(
                            "add jobs to relevant organizations so that agents can be assigned to source and/or target tasks" );
                } else {
                    CollaborationModel collaborationModel = communityService.getPlan();
                    Place locale = modelService.getPlanLocale();
                    List<Commitment> agreedTo = agreedToFilter( commitments, modelService );
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
                                } else if ( commonLanguageFilter( collaborationModel, commitments ).isEmpty() ) {
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
    private List<Commitment> agreedToFilter( List<Commitment> commitments, final ModelService modelService ) {
        return (List<Commitment>) CollectionUtils.select( commitments, new Predicate() {
            @Override
            public boolean evaluate( Object object ) {
                return modelService.isAgreedToIfRequired( (Commitment) object );
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
    private static List<Commitment> commonLanguageFilter( final CollaborationModel collaborationModel, List<Commitment> commitments ) {
        return (List<Commitment>) CollectionUtils.select( commitments, new Predicate() {
            @Override
            public boolean evaluate( Object object ) {
                return ( (Commitment) object ).isCommonLanguage( collaborationModel );
            }
        } );
    }

    @Override
    public String realizability( Commitment commitment, CommunityService communityService ) {
        ModelService modelService = communityService.getModelService();
        List<String> problems = findRealizabilityProblems( modelService.getCollaborationModel(), commitment, communityService );
        return problems.isEmpty() ?
                "Yes" :
                "No: " + StringUtils.capitalize( ChannelsUtils.listToString( problems, ", and " ) );
    }

    @Override
    public List<String> findRealizabilityProblems( CollaborationModel collaborationModel, Commitment commitment, CommunityService communityService ) {
        List<String> problems = new ArrayList<String>();
        List<TransmissionMedium> mediaUsed = commitment.getSharing().transmissionMedia();
        Place planLocale = communityService.getModelService().getPlanLocale();
/*
        if ( !communityService.isAgreedToIfRequired( commitment ) )
            problems.add( "sharing not agreed to as required" );
*/
        if ( !isAvailabilitiesCoincideIfRequired( commitment, mediaUsed, planLocale ) )
            problems.add( "availabilities do not coincide as they must" );
        if ( !commitment.isCommonLanguage( collaborationModel ) )
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
    public void commandDone( Commander commander, Command command, Change change ) {
        if ( !commander.isReplaying() && command.isTop() && !change.isNone() )
            if ( command.triggersAfterCommand() )
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
