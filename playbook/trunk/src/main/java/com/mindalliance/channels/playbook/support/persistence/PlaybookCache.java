package com.mindalliance.channels.playbook.support.persistence;

import com.opensymphony.oscache.base.Cache;
import com.opensymphony.oscache.base.CacheEntry;
import com.mindalliance.channels.playbook.ref.Ref;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 25, 2008
 * Time: 7:59:01 PM
 */
public class PlaybookCache extends Cache {

    public PlaybookCache(boolean useMemoryCaching, boolean unlimitedDiskCache, boolean overflowPersistence) {
        super(useMemoryCaching, unlimitedDiskCache, overflowPersistence);
    }

    public PlaybookCache(boolean useMemoryCaching, boolean unlimitedDiskCache, boolean overflowPersistence, boolean blocking, String algorithmClass, int capacity) {
        super(useMemoryCaching, unlimitedDiskCache, overflowPersistence, blocking, algorithmClass, capacity);
    }

    public boolean isFresh(Ref ref) {
        if (ref.isComputed())
            return true;
        else {
            CacheEntry cacheEntry = this.getCacheEntry(ref.getId(), null, null);
            boolean stale = isStale(cacheEntry, CacheEntry.INDEFINITE_EXPIRY, "");
            return !stale;
        }
    }

    public void clear() {
        super.clear();
    }
}