package com.mindalliance.channels.core.participation;

import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.orm.model.AbstractPersistentPlanObject;
import com.mindalliance.channels.core.query.QueryService;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Plan participation validation by supervisors.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 10/23/12
 * Time: 11:49 AM
 */
@Entity
public class PlanParticipationValidation extends AbstractPersistentPlanObject {

    @ManyToOne
    private PlanParticipation planParticipation;
    private Long supervisorId;

    public PlanParticipationValidation() {
    }

    public PlanParticipationValidation(
            PlanParticipation planParticipation,
            Long supervisorId,
            String username ) {
        super( planParticipation.getPlanUri(), planParticipation.getPlanVersion(), username );
        this.planParticipation = planParticipation;
        this.supervisorId = supervisorId;
    }

    public PlanParticipation getPlanParticipation() {
        return planParticipation;
    }

    public Long getSupervisorId() {
        return supervisorId;
    }

    public Actor getSupervisor( QueryService queryService ) {
        try {
            return queryService.find( Actor.class, supervisorId );
        } catch ( NotFoundException e ) {
            return null;
        }
    }
}
