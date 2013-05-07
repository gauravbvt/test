package com.mindalliance.channels.db.repositories;

import com.mindalliance.channels.db.data.messages.UserMessage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.stereotype.Repository;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/26/13
 * Time: 10:35 AM
 */
@Repository  // required for autowiring
public interface UserMessageRepository
        extends MongoRepository<UserMessage, String>, QueryDslPredicateExecutor<UserMessage> {
}
