package com.mindalliance.channels.db.repositories;

import com.mindalliance.channels.db.data.communities.RegisteredOrganization;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 6/19/13
 * Time: 1:33 PM
 */
@Repository
public interface RegisteredOrganizationRepository extends PagingAndSortingRepository<RegisteredOrganization,String>,
        QueryDslPredicateExecutor<RegisteredOrganization> {
}
