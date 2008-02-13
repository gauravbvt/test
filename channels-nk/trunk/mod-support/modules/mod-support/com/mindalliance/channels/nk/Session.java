// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.nk;

import org.ten60.netkernel.layer1.nkf.NKFException;

import com.ten60.netkernel.urii.aspect.IAspectBoolean;
import com.ten60.netkernel.urii.aspect.IAspectString;
import com.ten60.netkernel.urii.aspect.StringAspect;
import com.ten60.netkernel.urii.IURAspect;


public class Session {
    
    private String sessionURI;
    private ContextSupport ctx;
    
    public Session(ContextSupport  context) throws Exception {
        ctx = context;
        sessionURI = ctx.getArgument("session");
        if (!sessionURI.startsWith( "session:" )) 
            throw new Exception("Invalid session uri " + sessionURI);
    }

    public void storeToken(String token, IURAspect aspect) throws NKFException {
        ctx.subRequest(makeTokenURI(token)).
            ofType(ContextSupport.SINK).
            withSystemArg(aspect).
            issue();
    }

    public IURAspect recallToken(String token, Class<? extends IURAspect> aspectClass) throws NKFException {
        return ctx.subRequest(makeTokenURI(token)).transreptTo(aspectClass);
    }

    public void deleteToken(String token) throws NKFException {
        ctx.subRequest(makeTokenURI(token)).
            ofType(ContextSupport.DELETE).
            issue();
    }

    public boolean tokenExists(String token) throws NKFException {
        return ((IAspectBoolean)ctx.subRequest(makeTokenURI(token)).
                        ofType(ContextSupport.EXISTS).
                        issueForAspect(IAspectBoolean.class)).
                        isTrue();
    }

      /*public void storeToken(String token, IURAspect aspect) throws NKFException {
        String value = ((IAspectString)ctx.context.transrept(aspect, IAspectString.class)).getString();
        storeToken(token, value);
    }*/

    public void storeToken(String token, String value) throws NKFException {
        ctx.subRequest(makeTokenURI(token)).
            ofType(ContextSupport.SINK).
            withSystemArg(new StringAspect(value)).
            issue();
    }
    
    public String recallToken(String token) throws NKFException {
        String value = ((IAspectString)ctx.subRequest(makeTokenURI(token)).
                                            transreptTo(IAspectString.class)).getString();
        return value;
    }
    

    /**
     * Return the value of sessionURI.
     */
    public String getSessionURI() {
        return sessionURI;
    }
    
    private String makeTokenURI(String token) {
        return sessionURI + "+key@data:/" + token;
    }

    // Groovy support
    public void set(String token, String value) throws NKFException {
        if (value != null) {
            storeToken(token, value);
        } else {
            deleteToken(token);
        }
    }

    // Groovy support
    public void set(String token, IURAspect value) throws NKFException {
        if (value != null) {
            storeToken(token, value);
        } else {
            deleteToken(token);
        }
    }

    public Object get(String name) throws NKFException {
        if (name.endsWith("?")) {
            return tokenExists(name.substring(0, name.length() - 1));
        }
        return recallToken(name);
    }

}
