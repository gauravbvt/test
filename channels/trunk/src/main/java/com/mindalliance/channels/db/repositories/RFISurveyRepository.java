package com.mindalliance.channels.db.repositories;

import com.mindalliance.channels.db.data.surveys.RFISurvey;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/30/13
 * Time: 2:58 PM
 */
@Repository
public interface RFISurveyRepository
        extends PagingAndSortingRepository<RFISurvey,String>, QueryDslPredicateExecutor<RFISurvey> {
}
