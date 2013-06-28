package com.mindalliance.channels.db.repositories;

import com.mindalliance.channels.db.data.communities.UserParticipationConfirmation;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 6/20/13
 * Time: 12:03 PM
 */
@Repository
public interface UserParticipationConfirmationRepository
        extends PagingAndSortingRepository<UserParticipationConfirmation, String>,
        QueryDslPredicateExecutor<UserParticipationConfirmation> {
}
