package com.mindalliance.channels.db.repositories;

import com.mindalliance.channels.db.data.communities.OrganizationParticipation;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 6/19/13
 * Time: 2:43 PM
 */
@Repository
public interface OrganizationParticipationRepository extends PagingAndSortingRepository<OrganizationParticipation,String>,
        QueryDslPredicateExecutor<OrganizationParticipation> {
}
