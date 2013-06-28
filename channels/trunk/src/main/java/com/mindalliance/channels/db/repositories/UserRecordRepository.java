package com.mindalliance.channels.db.repositories;

import com.mindalliance.channels.db.data.users.UserRecord;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 6/21/13
 * Time: 7:14 PM
 */
@Repository
public interface UserRecordRepository
        extends PagingAndSortingRepository<UserRecord, String>,
        QueryDslPredicateExecutor<UserRecord> {

}
