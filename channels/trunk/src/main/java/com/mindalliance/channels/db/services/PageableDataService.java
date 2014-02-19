package com.mindalliance.channels.db.services;

import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.db.data.ChannelsDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoOperations;

import java.util.Map;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/18/14
 * Time: 4:20 PM
 */
public interface PageableDataService<T extends ChannelsDocument> extends DataService<T>  {

    Page<T> loadPage( Pageable pageable, PlanCommunity planCommunity );

    Page<T> loadPage( Pageable pageable, Map<String, Object> params, PlanCommunity planCommunity );

}
