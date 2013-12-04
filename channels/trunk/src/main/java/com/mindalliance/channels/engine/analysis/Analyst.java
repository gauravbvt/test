/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.engine.analysis;

import com.mindalliance.channels.core.command.CommandListener;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.model.Commitment;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.ModelEntity.Kind;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.model.TransmissionMedium;
import com.mindalliance.channels.engine.analysis.graph.EntityRelationship;
import com.mindalliance.channels.engine.analysis.graph.SegmentRelationship;
import com.mindalliance.channels.engine.imaging.ImagingService;

import java.util.List;

/**
 * Analyzes the plan and individual model elements.
 */
public interface Analyst extends CommandListener {
    /**
     * Get the doctor.
     * @return a doctor
     */
       Doctor getDoctor();

     /**
     * Find the causes for the part being conceptual, if any.
     *
     * @param communityService the query service
     * @param part         a part
     * @return a list of strings
     */
    List<String> findConceptualCausesInPlan( CommunityService communityService, Part part );

    /**
     * Find the causes for the flow being conceptual, if any.
     *
     * @param communityService the query service
     * @param flow         a flow
     * @return a list of strings
     */
    List<String> findConceptualCausesInPlan( CommunityService communityService, Flow flow );

    /**
     * Find remediations for the part being conceptual, if any.
     *
     * @param communityService the query service
     * @param part         a part
     * @return a list of strings
     */
    List<String> findConceptualRemediationsInPlan( CommunityService communityService, Part part );

    /**
     * Find remediations for the flow being conceptual, if any.
     *
     * @param communityService the query service
     * @param flow         a flow
     * @return a list of strings
     */
    List<String> findConceptualRemediationsInPlan( CommunityService communityService, Flow flow );

    /**
     * Find any relationship between an entity and an other. A relationship is one or more flow from the entity to the
     * other.
     *
     * @param communityService the query service
     * @param fromEntity   an entity
     * @param toEntity     an entity
     * @return an entity relationship or null if no link exists
     */
    <T extends ModelEntity> EntityRelationship<T> findEntityRelationship( CommunityService communityService, T fromEntity,
                                                                          T toEntity );

    /**
     * Find any relationship between an entity and an other within a segment. A relationship is one or more flow from
     * the entity to the other.
     *
     * @param communityService the query service
     * @param fromEntity   an entity
     * @param toEntity     an entity
     * @param segment      a segment
     * @return an entity relationship or null if no link exists
     */
    <T extends ModelEntity> EntityRelationship<T> findEntityRelationshipInPlan( CommunityService communityService, T fromEntity,
                                                                                T toEntity, Segment segment );

    /**
     * Find commitment relationships with entities of same kind.
     *
     * @param segment      a segment
     * @param entity       a model entity
     * @param communityService the query service
     * @return a list of relationships with other model entities
     */
    List<EntityRelationship> findEntityRelationshipsInPlan( Segment segment, ModelEntity entity, CommunityService communityService );

    /**
     * Find commitment relationships with entities of same kind referenced in a segment.
     *
     * @param communityService the query service
     * @param segment      a segment
     * @param entityClass  an entity class
     * @param kind         a kind of entity (actual or type)
     * @return a list of relationships with other model entities
     */
    List<EntityRelationship> findEntityRelationshipsInPlan( CommunityService communityService, Segment segment,
                                                            Class<? extends ModelEntity> entityClass, Kind kind );


    /**
     * Whether a commitment can be realized.
     * @param commitment a commitment
     * @param plan a plan
     * @param communityService a query service
     * @return a Boolean
     */
    Boolean canBeRealized( Commitment commitment, Plan plan, CommunityService communityService );

    /**
     * Find problem impeding realization of a commitment.
     *
     * @param plan       a plan
     * @param commitment a communication commitment
     * @param communityService a query service
     * @return a boolean
     */
    List<String> findRealizabilityProblems( Plan plan, Commitment commitment, CommunityService communityService );


    /**
     * Find any relationship between a plan segment and another. A relationship is one or more external flow in the
     * from-segment referencing a connector in the to-segment.
     *
     * @param communityService the query service
     * @param fromSegment  a plan segment
     * @param toSegment    a plan segment
     * @return a segment relationship or null if no link exists
     */
    SegmentRelationship findSegmentRelationship( CommunityService communityService, Segment fromSegment, Segment toSegment );

    /**
     * Get the imaging service.
     *
     * @return the imaging service
     */
    ImagingService getImagingService();

     /**
     * Whether a commitment could be met based on agent qualified to use medium..
     *
     * @param commitment a communication commitment
     * @param mediaUsed  media used in commitment
     * @param planLocale a plan locale
     * @return a boolean
     */
    Boolean isAgentsQualified( Commitment commitment, List<TransmissionMedium> mediaUsed, Place planLocale );

    /**
     * Whether a commitment could be met based on availability.
     *
     * @param commitment a communication commitment
     * @param mediaUsed  media used in commitment
     * @param planLocale a plan locale
     * @return a boolean
     */
    Boolean isAvailabilitiesCoincideIfRequired( Commitment commitment, List<TransmissionMedium> mediaUsed,
                                                Place planLocale );

    /**
     * Whether a part is effectively conceptual.
     *
     * @param communityService the query service
     * @param part         a part
     * @return a boolean
     */
    Boolean isEffectivelyConceptualInPlan( CommunityService communityService, Part part );

    /**
     * Whether a flow is effectively conceptual.
     *
     * @param communityService the query service
     * @param flow         a flow
     * @return a boolean
     */
    Boolean isEffectivelyConceptualInPlan( CommunityService communityService, Flow flow );

    /**
     * Whether a commitment could be met based on known contact info.
     *
     * @param commitment a communication commitment
     * @param mediaUsed  media used in commitment
     * @param planLocale a plan locale
     * @return a boolean
     */
    boolean isReachable( Commitment commitment, List<TransmissionMedium> mediaUsed, Place planLocale );


    /**
     * Notification of command execution.
     *
     * @param planCommunity a plan community
     */
    void onAfterCommand( PlanCommunity planCommunity );

    /**
     * On destroy.
     */
    void onDestroy();

    /**
     * On startup.
     *
     * @param planCommunity a plan community
     */
    void onStart( PlanCommunity planCommunity );

    /**
     * On stop.
     */
    void onStop();

    /**
     * Diagnostic about whether a commitment can be realized or is conceptual.
     *
     * @param commitment   a communication commitment
     * @param communityService a community service
     * @return a string
     */
    String realizability( Commitment commitment, CommunityService communityService );


}
