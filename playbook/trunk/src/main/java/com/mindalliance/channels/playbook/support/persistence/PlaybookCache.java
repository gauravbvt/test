package com.mindalliance.channels.playbook.support.persistence;

import com.opensymphony.oscache.base.Cache;
import com.opensymphony.oscache.base.CacheEntry;
import com.opensymphony.oscache.base.NeedsRefreshException;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.ref.impl.BeanImpl;
import com.mindalliance.channels.playbook.support.PlaybookSession;
import com.mindalliance.channels.playbook.mem.RefLockException;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import org.apache.log4j.Logger;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 25, 2008
 * Time: 7:59:01 PM
 */
public class PlaybookCache extends Cache {

    Map<Ref, PlaybookSession> locks = new HashMap<Ref, PlaybookSession>();

    public PlaybookCache(boolean useMemoryCaching, boolean unlimitedDiskCache, boolean overflowPersistence) {
        super(useMemoryCaching, unlimitedDiskCache, overflowPersistence);
    }

    public PlaybookCache(boolean useMemoryCaching, boolean unlimitedDiskCache, boolean overflowPersistence, boolean blocking, String algorithmClass, int capacity) {
        super(useMemoryCaching, unlimitedDiskCache, overflowPersistence, blocking, algorithmClass, capacity);
    }

    public void sessionTimedOut(PlaybookSession session) {
        cleanupLocks(session);
    }

    public Object getFromCache(String key, int refreshPeriod) throws NeedsRefreshException {
        Object obj = super.getFromCache(key, refreshPeriod);
        return BeanImpl.makeClone(obj);
    }

    public boolean isFresh(Ref ref) {
        if (ref.isComputed()) return true;   // TODO remove -- should be redundant
        if (ref.getId() == null) {
            Logger.getLogger(this.getClass()).warn("Ref with null id");
            return false;
        }
        CacheEntry cacheEntry = this.getCacheEntry(ref.getId(), null, null);
        boolean stale = isStale(cacheEntry, CacheEntry.INDEFINITE_EXPIRY, "");
        return !stale;
    }

    // always called within a synchroized(ApplicationMemory) block
    public void clear() {
        super.clear();
        locks = new HashMap<Ref, PlaybookSession>();
    }

    // always called within a synchroized(ApplicationMemory) block
    public void lock(Ref ref, PlaybookSession session) {
        if (isLocked(ref, session)) throw new RefLockException("Can't lock $ref - locked by another session");
        locks.put(ref, session);
    }

    // always called within a synchroized(ApplicationMemory) block
    public void unlock(Ref ref, PlaybookSession session) {
        if (isLocked(ref, session)) throw new RefLockException("Can't unlock $ref - locked by another session");
        locks.remove(ref);
    }

    // always called within a synchroized(ApplicationMemory) block
    public boolean isLocked(Ref ref, PlaybookSession session) {
        PlaybookSession owner = locks.get(ref);
        if (owner != null) {
            if (owner == session) {
                return false;
            } else {
                if (owner.isSessionInvalidated()) {
                    cleanupLocks(owner); // lazily triggered locks cleanup
                    return false;
                } else {
                    return true;
                }
            }
        } else {
            return false;
        }
    }

    private void cleanupLocks(PlaybookSession invalidatedSession) {     // TODO - how about cleaning up ALL invlidated locks?
        Logger.getLogger(this.getClass()).info("Releasing all locks from invalidated session " + invalidatedSession);
        List<Ref> expiredLocks = new ArrayList<Ref>();
        for (Map.Entry<Ref, PlaybookSession> entry : locks.entrySet()) {
            if (entry.getValue() == invalidatedSession) {
                expiredLocks.add(entry.getKey());
            }
        }
        for (Ref ref : expiredLocks) {
            locks.remove(ref);
        }
    }

}