package com.mindalliance.channels.db.repositories;

import com.mindalliance.channels.db.data.messages.Feedback;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/25/13
 * Time: 10:33 PM
 */
@Repository   // required for autowiring
public interface FeedbackRepository
        extends PagingAndSortingRepository<Feedback, String>, QueryDslPredicateExecutor<Feedback> {
}
