package com.mindalliance.channels.db.services;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.db.data.ChannelsDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/25/13
 * Time: 3:40 PM
 */
public interface DataService<T extends ChannelsDocument> {

    public static final long LOCK_TIMEOUT = 5 * 60 * 1000; // 5 minutes in msecs

    MongoOperations getDb();

    void save(T object);

    T load( String uid );

    T refresh( T channelsDocument );

    boolean lock( T channelsDocument, String username, CommunityService communityService );

    long getLockTimeout();

    ChannelsUser getLockOwner( T channelsDocument, CommunityService communityService );

    void unlock( T channelsDocument, String username, CommunityService communityService );

}
