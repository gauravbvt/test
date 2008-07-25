package com.mindalliance.channels.playbook.mem

import com.mindalliance.channels.playbook.support.PlaybookApplication
import com.mindalliance.channels.playbook.ref.Ref
import org.apache.log4j.Logger

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 23, 2008
 * Time: 5:17:32 PM
 */
class LockManager {

    static final LockManager singleton = new LockManager()

    static String getOwner(Ref ref) {
        return PlaybookApplication.current().getAppMemory().getOwner(ref)
    }

    static boolean lock(Ref ref) {
        return PlaybookApplication.current().getAppMemory().lock(ref)
    }

    static boolean unlock(Ref ref) {
        return PlaybookApplication.current().getAppMemory().unlock(ref)
    }

    static boolean isReadWrite(Ref ref) {
        return PlaybookApplication.current().getAppMemory().isReadWrite(ref)
    }

    static boolean isReadOnly(Ref ref) {
        return PlaybookApplication.current().getAppMemory().isReadOnly(ref)   // this session has ref as readOnly or some other sessions has a lock on the Ref
    }

    // Attempts to acquire a lock on list of refs. Returns whether successful
    // If failed, releases any lock acquired in the attempt
    static boolean lockAll(List<Ref> refs) {
        boolean allLocked = false
        List<Ref> newLocks = []
        try {
            (List<Ref>) refs.each {ref ->
                if (ref as boolean && lock(ref)) newLocks.add(ref)
            }
            allLocked = true
        }
        catch (RefLockException e) {
            Logger.getLogger(LockManager.class).info("Unable to lock all of $refs")
            newLocks.each {ref -> unlock(ref)}
        }
        return allLocked
    }
}