package com.mindalliance.channels.playbook.support.persistence;

import com.mindalliance.channels.playbook.support.PlaybookSession;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.ifm.User;

import java.io.Serializable;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 22, 2008
 * Time: 2:03:50 PM
 */
public class Lock implements Serializable {

    static final long MAX_TIMEOUT = Long.MAX_VALUE;   // TODO set this to a reasonable value

    Ref ref;
    PlaybookSession session = PlaybookSession.current();
    long start = System.currentTimeMillis();
    long timeout = MAX_TIMEOUT;
    private static final long serialVersionUID = -1626963653289024733L;

    Lock(Ref ref) {
        this.ref = ref;
    }

    Lock(Ref ref, long timeout) {
        this.timeout = timeout;
    }

    boolean isTimedOut() {
        return System.currentTimeMillis() < (start + Math.min(MAX_TIMEOUT, timeout));
    }

    String getOwner() {
        User user = (User)session.getUser().deref();
        return user.getName();
    }
}