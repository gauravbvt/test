package com.mindalliance.channels.core.participation;

import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.orm.service.GenericSqlService;

import java.util.List;

/**
 * Supervised plan participation validation service.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 10/23/12
 * Time: 12:00 PM
 */
public interface PlanParticipationValidationService extends GenericSqlService<PlanParticipationValidation,Long> {

    List<PlanParticipationValidation> getParticipationValidations( Plan plan );

    List<PlanParticipationValidation> getParticipationValidations( PlanParticipation planParticipation );

    List<PlanParticipationValidation> getParticipationValidations( Plan plan, Actor supervisor );

    void addParticipationValidation(
            PlanParticipation planParticipation,
            Actor supervisor,
            ChannelsUser user
    );

    void removeParticipationValidation(
            PlanParticipation planParticipation,
            Actor supervisor );

    boolean isValidatedBy( PlanParticipation planParticipation, Actor supervisor );

    void deleteValidations( PlanParticipation participation );
}
