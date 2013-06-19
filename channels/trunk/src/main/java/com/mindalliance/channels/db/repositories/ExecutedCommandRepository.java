package com.mindalliance.channels.db.repositories;

import com.mindalliance.channels.db.data.activities.ExecutedCommand;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 6/18/13
 * Time: 5:23 PM
 */
@Repository
public interface ExecutedCommandRepository extends PagingAndSortingRepository<ExecutedCommand,String>,
        QueryDslPredicateExecutor<ExecutedCommand> {
}