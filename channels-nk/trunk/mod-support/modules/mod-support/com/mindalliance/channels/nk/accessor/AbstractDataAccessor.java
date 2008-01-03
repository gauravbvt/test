package com.mindalliance.channels.nk.accessor;

import org.ten60.netkernel.layer1.nkf.INKFRequestReadOnly;
import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper;
import org.ten60.netkernel.layer1.nkf.impl.NKFAccessorImpl;

/**
 * A simple data accessor.  Override the desired protected method to
 * implement behavior.
 */
public class AbstractDataAccessor extends NKFAccessorImpl {

    public AbstractDataAccessor() {
        super(SAFE_FOR_CONCURRENT_USE, INKFRequestReadOnly.RQT_SOURCE |
                INKFRequestReadOnly.RQT_SINK |
                INKFRequestReadOnly.RQT_NEW |
                INKFRequestReadOnly.RQT_DELETE
        );
    }

    public void processRequest(INKFConvenienceHelper context) throws Exception {
        switch (context.getThisRequest().getRequestType()) {
            case INKFRequestReadOnly.RQT_SOURCE:
                source(context);
                break;
            case INKFRequestReadOnly.RQT_NEW:
                create(context);
                break;
            case INKFRequestReadOnly.RQT_SINK:
                sink(context);
                break;
            case INKFRequestReadOnly.RQT_DELETE:
                delete(context);
                break;
            case INKFRequestReadOnly.RQT_EXISTS:
                exists(context);
                break;
            default:
                throw new Exception("Invalid request type");
        }
    }

    protected void source(INKFConvenienceHelper context) throws Exception {
        throw new Exception("Request type not implemented");
    }

    protected void sink(INKFConvenienceHelper context) throws Exception {
        throw new Exception("Request type not implemented");
    }

    protected void create(INKFConvenienceHelper context) throws Exception {
        throw new Exception("Request type not implemented");
    }

    protected void delete(INKFConvenienceHelper context) throws Exception {
        throw new Exception("Request type not implemented");
    }

    protected void exists(INKFConvenienceHelper context) throws Exception {
        throw new Exception("Request type not implemented");
    }

}
