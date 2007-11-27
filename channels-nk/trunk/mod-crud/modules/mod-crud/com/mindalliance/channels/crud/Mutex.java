// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.crud;

import org.ten60.netkernel.layer1.nkf.NKFException;

import com.mindalliance.channels.nk.ContextSupport;
import com.mindalliance.channels.nk.ContextSupport.Request;


public class Mutex {

    private boolean LOG_MUTEX = true;
    private ContextSupport ctx;
    
    public Mutex(ContextSupport contextSupport) {
        ctx = contextSupport;
    }

    private void issueMutexRequest(String command, String who) throws NKFException {
        Request req = ctx.subRequest( "active:crud_mrew" ).
            withString("operator", command);
        if (LOG_MUTEX) ctx.log(who + ": Start " + command, "info");
        req.issue();
        if (LOG_MUTEX) ctx.log(who + ": End " + command, "info");
    }

//     Multiple reads, exclusive write

    public void beginRead(String who) throws NKFException {
        issueMutexRequest("beginRead", who);
    }

    public void endRead(String who) throws NKFException {
        issueMutexRequest("endRead", who);
    }

    public void beginWrite(String who) throws NKFException {
        issueMutexRequest("beginWrite", who);
    }

    public void endWrite(String who) throws NKFException {
        issueMutexRequest("endWrite", who);
    }
    
}
