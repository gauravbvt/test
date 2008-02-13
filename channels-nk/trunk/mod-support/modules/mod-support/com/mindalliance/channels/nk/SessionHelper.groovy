package com.mindalliance.channels.nk

import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper as Context
import com.ten60.netkernel.urii.IURAspect

/**
 * Created by IntelliJ IDEA.
 * User: jf
 * Date: Feb 12, 2008
 * Time: 1:14:58 PM
 * To change this template use File | Settings | File Templates.
 */
class SessionHelper {

    private Context ctx
    String sessionURI

    SessionHelper(Context context) {
        String uri = context.getThisRequest().getArgument('session')
        ctx = context
        if (!uri.startsWith("session:")) throw new Exception("Invalid session uri " + uri)
        sessionURI = uri
    }

    SessionHelper(String uri, Context context) {
        ctx = context
        if (!uri.startsWith("session:")) throw new Exception("Invalid session uri " + uri)
        sessionURI = uri
    }

    void storeToken(String token, IURAspect aspect) {
        use(NetKernelCategory) {
            ctx.subrequest(makeTokenURI(token), [type: 'sink', SYSTEM: aspect])
        }
    }

    void storeToken(String token, String value) {
        use(NetKernelCategory) {
            ctx.subrequest(makeTokenURI(token), [type: 'sink', SYSTEM: string(value)])
        }
    }
    IURAspect recallToken(String token, Class aspectClass) {
        IURAspect aspect
        use(NetKernelCategory) {
            aspect = ctx.sourceAspect(makeTokenURI(token), aspectClass)
        }
        return aspect
    }

    void deleteToken(String token) {
        use(NetKernelCategory) {
            ctx.subrequest(makeTokenURI(token), [type: 'delete'])
        }
    }

    boolean tokenExists(String token) {
        boolean exists
        use(NetKernelCategory) {
            exists = ctx.isTrue(makeTokenURI(token), [type: 'exists'])
        }
        return exists
    }

    String recallToken(String token) {
        String value
        use(NetKernelCategory) {
            value = ctx.sourceString(makeTokenURI(token))
        }
        return value
    }


    private String makeTokenURI(String token) {
        return sessionURI + "+key@data:/" + token;
    }


    void set(String token, Object value) {
        if (value != null) {
            storeToken(token, value);
        } else {
            deleteToken(token);
        }
    }
    //
    //    void set(String token, IURAspect value) {
    //        if (value != null) {
    //            storeToken(token, value);
    //        } else {
    //            deleteToken(token);
    //        }
    //    }
    //    void set(String token, Object value) {
    //        if (value == null) {
    //            deleteToken(value);
    //        } else if (value instanceof String
    //            || value instanceof IURAspect) {
    //            storeToken(token, value);
    //        }
    //    }


    Object get(String name) {
        if (name.endsWith("?")) {
            return tokenExists(name.substring(0, name.length() - 1))
        }
        return recallToken(name)
    }

}