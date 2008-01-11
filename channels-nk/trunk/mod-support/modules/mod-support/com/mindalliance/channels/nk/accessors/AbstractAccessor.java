package com.mindalliance.channels.nk.accessors;

import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper;
import org.ten60.netkernel.layer1.nkf.INKFRequestReadOnly;
import org.ten60.netkernel.layer1.nkf.impl.NKFAccessorImpl;

/**
 *  A simple accessor template that only handles source requests.
 * Override source() to implement behavior.
 */
public class AbstractAccessor extends NKFAccessorImpl {

    public AbstractAccessor() {
        super(AbstractAccessor.SAFE_FOR_CONCURRENT_USE, INKFRequestReadOnly.RQT_SOURCE);
    }

    public void processRequest(INKFConvenienceHelper context) throws Exception {
        switch (context.getThisRequest().getRequestType()) {
            case INKFRequestReadOnly.RQT_SOURCE:
                source(context);
                break;
            default:
                throw new Exception("Invalid request type");
        }
    }

    protected void source(INKFConvenienceHelper context) throws Exception {
        throw new Exception("Request type not implemented");
    }
}
