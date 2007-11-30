// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.nk;

import org.ten60.netkernel.layer1.nkf.NKFException;

import com.mindalliance.channels.nk.ContextSupport;
import com.ten60.netkernel.urii.aspect.IAspectBoolean;
import com.ten60.netkernel.urii.aspect.IAspectString;
import com.ten60.netkernel.urii.aspect.StringAspect;


public class Session {
    
    private String sessionURI;
    private ContextSupport ctx;
    
    public Session(ContextSupport  context) throws Exception {
        ctx = context;
        sessionURI = ctx.getArgument("session");
        if (!sessionURI.startsWith( "session:" )) 
            throw new Exception("Invalid session uri " + sessionURI);
    }

    public void storeToken(String token, String value) throws NKFException {
        ctx.subRequest(makeTokenURI(token)).
            ofType(ContextSupport.SINK).
            withSystemArg(new StringAspect(value)).
            issue();
    }
    
    public boolean tokenExists(String token) throws NKFException {
        return ((IAspectBoolean)ctx.subRequest(makeTokenURI(token)).
                        ofType(ContextSupport.EXISTS).
                        issueForAspect(IAspectBoolean.class)).
                        isTrue();
    }
    
    public String recallToken(String token) throws NKFException {
        String value = ((IAspectString)ctx.subRequest(makeTokenURI(token)).
                                            transreptTo(IAspectString.class)).getString();
        return value;
    }
    
    public void deleteToken(String token) throws NKFException {
        ctx.subRequest(makeTokenURI(token)).
            ofType(ContextSupport.DELETE).
            issue();
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

}
