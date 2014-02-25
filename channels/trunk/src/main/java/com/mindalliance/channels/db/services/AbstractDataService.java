package com.mindalliance.channels.db.services;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.db.data.ChannelsDocument;
import com.mindalliance.channels.db.data.DataLock;
import org.apache.commons.collections.IteratorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/25/13
 * Time: 2:55 PM
 */
public abstract class AbstractDataService<T extends ChannelsDocument> implements DataService<T> {

    @Autowired
    private MongoTemplate mongoTemplate;

    public MongoOperations getDb() {
        return mongoTemplate;
    }

    @Override
    public T refresh( T channelsDocument ) {
        return channelsDocument == null ? null : load( channelsDocument.getUid() );
    }

    protected int toInteger( long value ) {
        return Integer.parseInt( Long.toString( value ) );
    }

    @SuppressWarnings( "unchecked" )
    protected List<T> toList( Iterable<T> iterable ) {
        return IteratorUtils.toList( iterable.iterator() );
    }

    @Override
    public boolean lock( T channelsDocument, String username, CommunityService communityService ) {
        PlanCommunity planCommunity = communityService.getPlanCommunity();
        synchronized ( planCommunity ) {
            DataLock dataLock = channelsDocument.getDataLock();
            if ( dataLock == null ) {
                channelsDocument.setDataLock( new DataLock( username ) );
                save( channelsDocument );
                return true;
            } else {
                if ( dataLock.isOwnedBy( username ) ) {
                    dataLock.refresh();
                    save( channelsDocument );
                    return true;
                } else {
                    if ( dataLock.isExpired( getLockTimeout() ) ) {
                        channelsDocument.setDataLock( new DataLock( username ) );
                        save( channelsDocument );
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        }
    }

    @Override
    public long getLockTimeout() {
        return LOCK_TIMEOUT;
    }

    @Override
    public void unlock( T channelsDocument, String username, CommunityService communityService ) {
        PlanCommunity planCommunity = communityService.getPlanCommunity();
        synchronized ( planCommunity ) {
            DataLock dataLock = channelsDocument.getDataLock();
            if ( dataLock != null && dataLock.isOwnedBy( username ) ) {
                channelsDocument.setDataLock( null );
                save( channelsDocument );
            }
        }
    }

    @Override
    public ChannelsUser getLockOwner( T channelsDocument, CommunityService communityService ) {
        PlanCommunity planCommunity = communityService.getPlanCommunity();
        synchronized ( planCommunity ) {
            DataLock dataLock = channelsDocument.getDataLock();
            if ( dataLock != null ) {
                return communityService.getUserRecordService().getUserWithIdentity( dataLock.getUsername() );
            } else {
                return null;
            }
        }
    }
}
