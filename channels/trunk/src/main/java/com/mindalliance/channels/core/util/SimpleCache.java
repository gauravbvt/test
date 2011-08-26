package com.mindalliance.channels.core.util;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple cache with expiration.
 *
 * @param <K> the keys' class
 * @param <V> the values' class
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Dec 15, 2008
 * Time: 6:06:06 PM
 */
public class SimpleCache<K, V> {
    /**
     * A timestamped cache entry
     */
    private class Entry<V> {
        /**
         * Cache value
         */
        private V value;
        /**
         * Value's timestamp
         */
        private Date timestamp;

        /**
         * Constructor
         *
         * @param value -- value cached
         */
        Entry( V value ) {
            this.value = value;
            timestamp = new Date();
        }

        public V getValue() {
            return value;
        }

        public Date getTimestamp() {
            return timestamp;
        }
    }

    /**
     * Cached entries
     */
    private Map<K, Entry<V>> entries = Collections.synchronizedMap( new HashMap<K, Entry<V>>() );

    public SimpleCache() {
    }

    /**
     * Add value to the cache
     *
     * @param key    -- key
     * @param object -- value cached at key
     */
    public void put( K key, V object ) {
        entries.put( key, new Entry<V>( object ) );
    }

    /**
     * return value cached at key if timestamp after given date, else null
     *
     * @param key          -- key
     * @param validIfAfter -- value must be cached after this date, else invalid
     * @return cached value or null
     */
    public V get( K key, Date validIfAfter ) {
        V value = null;
        Entry<V> entry = entries.get( key );
        if ( entry != null ) {
            if ( validIfAfter == null || !entry.getTimestamp().before( validIfAfter ) ) {
                value = entry.value;
            } else {
                entries.remove( key );
            }
        }
        return value;
    }
}
