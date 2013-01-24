package com.mindalliance.channels.core.dao;

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

    /**
     * Start id for immutable (constant) model objects.
     */
    private static final long IMMUTABLE_LOW = -1000L; // WARNING: Assumes no more than 1000 immutable objects!

    /**
     * Start id for mutable (constant) model objects.
     */
    private static final long MUTABLE_LOW = 0L;

    /**
     * Use mutable object id range by default.
     */
    private boolean mutableModeSet = true;

    /**
     * Last assigned ids for mutable objects.
     */
    private Map<String, Long> lastIds = new HashMap<String, Long>();
    /**
     * Last assigned ids for immutable objects.
     */
    private Map<String, Long> lastImmutableIds = new HashMap<String, Long>();

    public PerPlanIdGenerator() {
    }

    public boolean isMutableModeSet() {
        return mutableModeSet;
    }

    public long getLastAssignedId( String uri ) {
        return getLastId( uri );
    }

    public void setLastAssignedId( long id, String uri ) {
        setLastId( id, uri );
    }

    public synchronized long assignId( Long id, String uri ) {
        long lastId = id == null ? getLastId( uri ) + 1L
                                 : Math.max( getLastId( uri ), id );

        setLastId( lastId, uri );

        return id == null ? lastId : id;
    }

    @Override
    public void setImmutableMode() {
        mutableModeSet = false;
    }

    @Override
    public void setMutableMode() {
        mutableModeSet = true;
    }

    private Map<String, Long> getLastIds() {
        return mutableModeSet ? lastIds : lastImmutableIds;
    }

    private synchronized long getLastId( String uri ) {
        Long lastId = getLastIds().get( uri );
        if ( lastId == null ) {
            lastId = mutableModeSet ? MUTABLE_LOW : IMMUTABLE_LOW;
            getLastIds().put( uri, lastId );
        }
        return lastId;
    }

    private synchronized void setLastId( Long id, String uri ) {
        getLastId( uri );
        getLastIds().put( uri, id );
    }
}
