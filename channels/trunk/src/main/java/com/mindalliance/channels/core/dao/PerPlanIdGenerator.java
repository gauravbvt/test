package com.mindalliance.channels.core.dao;

import com.mindalliance.channels.core.model.Plan;

import java.util.HashMap;
import java.util.Map;

/**
 * Per plan id generator (one last assigned id per plan).
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Aug 12, 2009
 * Time: 5:07:07 PM
 */
public class PerPlanIdGenerator implements IdGenerator {

    private Map<Integer, Long> lastIds = new HashMap<Integer, Long>();

    public PerPlanIdGenerator() {
    }

    public long getLastAssignedId( Plan plan ) {
        return getLastId( plan );
    }

    public void setLastAssignedId( long id, Plan plan ) {
        setLastId( id, plan );
    }

    public synchronized long assignId( Long id, Plan plan ) {
        long lastId = id == null ? getLastId( plan ) + 1L
                                 : Math.max( getLastId( plan ), id );

        setLastId( lastId, plan );

        return id == null ? lastId : id;
    }

    private synchronized long getLastId( Plan plan ) {
        Long lastId = lastIds.get( plan.systemHashCode() );
        if ( lastId == null ) {
            lastId = 0L;
            lastIds.put( plan.systemHashCode(), lastId );
        }
        return lastId;
    }

    private synchronized void setLastId( Long id, Plan plan ) {
        getLastId( plan );
        lastIds.put( plan.systemHashCode(), id );
    }
}
