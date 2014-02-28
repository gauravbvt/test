package com.mindalliance.channels.core.dao;

import java.util.HashMap;
import java.util.Map;

/**
 * Default id generator (one last assigned id per model or community).
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Aug 12, 2009
 * Time: 5:07:07 PM
 */
public class DefaultIdGenerator implements IdGenerator {

    /**
     * Start id for immutable (constant) model objects.
     */
    private static final long IMMUTABLE_LOW = -1000L; // WARNING: Assumes no more than 1000 immutable objects!

    /**
     * Use mutable object id range by default.
     */
    private boolean mutableModeSet = true;

    /**
     * Last assigned ids for mutable objects.
     */
    private Map<String, Long> idCounters = new HashMap<String, Long>();
    /**
     * Last assigned ids for immutable objects.
     */
    private Map<String, Long> immutableIdCounters = new HashMap<String, Long>();

    private long idShift = 0L;

    public DefaultIdGenerator() {
    }

    public boolean isMutableModeSet() {
        return mutableModeSet;
    }

    public long getIdCounter( String uri ) {
        return getIdCountFor( uri );
    }

    public void setIdCounter( long id, String uri ) {
        setIdCountFor( id, uri );
    }

    public synchronized long assignId( Long id, String uri ) {
        Long shiftedId = id == null ? null : getShiftedId( id );
        long lastId = shiftedId == null ? getIdCountFor( uri ) + 1L
                                 : Math.max( getIdCountFor( uri ) + 1L, shiftedId );

        setIdCountFor( lastId, uri );

        return shiftedId == null ? lastId : shiftedId;
    }

    @Override
    public void setImmutableMode() {
        mutableModeSet = false;
    }

    @Override
    public void setMutableMode() {
        mutableModeSet = true;
    }

    @Override
    public void setTemporaryIdShift( long idShift ) {
        this.idShift = idShift;
    }

    @Override
    public void cancelTemporaryIdShift() {
        idShift = 0L;
    }

    @Override
    public long getShiftedId( long id ) {
        return id + ( id >= MUTABLE_LOW ? getIdShift() : 0L );
    }

    private long getIdShift() {
        return mutableModeSet ? idShift : 0L;
    }

    private Map<String, Long> getIdCounters() {
        return mutableModeSet ? idCounters : immutableIdCounters;
    }

    private synchronized long getIdCountFor( String uri ) {
        Long lastId = getIdCounters().get( uri );
        if ( lastId == null ) {
            lastId = mutableModeSet ? MUTABLE_LOW : IMMUTABLE_LOW;
            getIdCounters().put( uri, lastId );
        }
        return lastId;
    }

    private synchronized void setIdCountFor( Long id, String uri ) {
        getIdCountFor( uri );
        getIdCounters().put( uri, id );
    }
}
