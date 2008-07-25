package com.mindalliance.channels.playbook.support.persistence;

import com.opensymphony.oscache.base.Cache;
import com.opensymphony.oscache.base.CacheEntry;
import com.opensymphony.oscache.base.NeedsRefreshException;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.ref.Referenceable;
import com.mindalliance.channels.playbook.ref.impl.BeanImpl;
import com.mindalliance.channels.playbook.support.PlaybookSession;
import com.mindalliance.channels.playbook.mem.RefLockException;
import com.mindalliance.channels.playbook.ifm.User;

import java.util.*;

import org.apache.log4j.Logger;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 25, 2008
 * Time: 7:59:01 PM
 */
public class PlaybookCache extends Cache {

    Map<Ref, Lock> locks = Collections.synchronizedMap(new HashMap<Ref, Lock>());

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

    public boolean isStored(Ref ref) {
        if (ref.getId() == null) {
            Logger.getLogger(this.getClass()).warn("Ref with null id");
            return false;
        }
        CacheEntry cacheEntry = this.getCacheEntry(ref.getId(), null, null);
        boolean stale = isStale(cacheEntry, CacheEntry.INDEFINITE_EXPIRY, "");
        return !stale;
    }

    public boolean isFresh(Ref ref) {
        if (isStored(ref)) { // if tries to get from cache a stale entry, cache may get into a wait state, believing another thread is busy updating
            try {
                Referenceable referenceable = (Referenceable) getFromCache(ref.getId(), CacheEntry.INDEFINITE_EXPIRY);
                ref.attach(referenceable);  // If fresh, attaches deref-ed value to ref
                return true;
            }
            catch (NeedsRefreshException e) {
                ref.detach();
                return false;
            }
        } else {
            ref.detach();
            return false;
        }
    }

    // always called within a synchroized(ApplicationMemory) block
    public void clear() {
        super.clear();
        locks = new HashMap<Ref, Lock>();
    }

    // always called within a synchronized(ApplicationMemory) block
    public boolean lock(Ref ref) {
        if (isReadOnly(ref)) throw new RefLockException("Can't lock $ref - locked by another session");
        if (!isReadWrite(ref)) {
            locks.put(ref, new Lock(ref));
            Logger.getLogger(this.getClass()).info("Locked " + ref);
            return true;
        } else {
            return false;
        }
    }

    // always called within a synchroized(ApplicationMemory) block
    public boolean unlock(Ref ref) {
        if (isReadOnly(ref)) throw new RefLockException("Can't unlock $ref - locked by another session");
        if (isReadWrite(ref)) {
            Logger.getLogger(this.getClass()).info("Unlocked " + ref);
            locks.remove(ref);
            return true;
        } else {
            return false;
        }
    }

    // always called within a synchroized(ApplicationMemory) block
    public boolean isReadOnly(Ref ref) {
        Lock lock = locks.get(ref);
        if (lock == null) return false;
        if (lock.session == PlaybookSession.current()) return false;
        if (lock.isTimedOut()) {
            locks.remove(ref);
            return false;
        }
        if (lock.session.isSessionInvalidated()) {
            cleanupLocks(lock.session); // lazily triggered locks cleanup
            return false;
        }
        return true;
    }

    public boolean isReadWrite(Ref ref) {
        Lock lock = locks.get(ref);
        if (lock == null) return false;
        if (lock.session != PlaybookSession.current()) return false;
        if (lock.isTimedOut()) {
            locks.remove(ref);
            return false;
        }
        if (lock.session.isSessionInvalidated()) {
            cleanupLocks(lock.session); // lazily triggered locks cleanup
            return false;
        }
        return true;

    }

    public String getOwner(Ref ref) {
        String owner = null;
        Lock lock = locks.get(ref);
        if (lock != null) {
            User user = (User) lock.session.getUser().deref();
            if (user != null) owner = user.getName();
        }
        return owner;
    }

    private void cleanupLocks(PlaybookSession invalidatedSession) {
        synchronized (this) {
            Logger.getLogger(this.getClass()).info("Releasing all locks from invalidated session " + invalidatedSession);
            List<Ref> expiredLocks = new ArrayList<Ref>();
            for (Map.Entry<Ref, Lock> entry : locks.entrySet()) {
                Lock lock = entry.getValue();
                if (lock.session == invalidatedSession) {
                    expiredLocks.add(entry.getKey());
                }
            }
            for (Ref ref : expiredLocks) {
                locks.remove(ref);
            }
        }
    }

}