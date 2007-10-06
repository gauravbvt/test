// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data;

import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper;
import org.ten60.netkernel.layer1.nkf.INKFRequest;
import org.ten60.netkernel.layer1.nkf.NKFException;
import org.ten60.netkernel.layer1.representation.StringAspect;

import com.mindalliance.channels.nk.ContextHelper;


public class Mutex {

    private boolean LOG_MUTEX = true;
    private ContextHelper contextHelper;
    
    public Mutex(INKFConvenienceHelper context) {
        contextHelper = new ContextHelper(context);
    }

    private void issueMutexRequest(String command, String who) throws NKFException {
        INKFRequest req = contextHelper.context.createSubRequest("active:MREWSynchronizer");
        req.addArgument("operator", new StringAspect(command));
        if (LOG_MUTEX) contextHelper.log(who + ": Start " + command, "info");
        contextHelper.context.issueSubRequest(req);
        if (LOG_MUTEX) contextHelper.log(who + ": End " + command, "info");
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
