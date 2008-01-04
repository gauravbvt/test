// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.modeler.accessors;

import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper;
import org.ten60.netkernel.layer1.nkf.impl.NKFAccessorImpl;
import org.ten60.netkernel.layer1.representation.BooleanAspect;

import com.mindalliance.channels.nk.Session;
import com.mindalliance.channels.nk.ContextSupport;


public class SessionValidator  extends NKFAccessorImpl {

    public SessionValidator() {
        super(4,false,ContextSupport.SOURCE);
    }
    
    @Override
    public void processRequest( INKFConvenienceHelper context ) throws Exception {
        ContextSupport ctx = new ContextSupport(context);
        switch (ctx.requestType()) {
        case ContextSupport.SOURCE:
            source(ctx);
            break;
        default: throw new Exception("Invalid request type");
        }
    }
        
    private void source( ContextSupport ctx ) throws Exception {
        Session session = new Session(ctx);
        boolean isValid;
        if(session.tokenExists("credentials")) {
            String credentials = session.recallToken("credentials");
            // Validate Credentials
            isValid = validateCredentials( session.getSessionURI(), credentials );
        }
        else {
            // No credentials exist for this session
            isValid = false;
        }
        ctx.respond(new BooleanAspect(isValid)).
            setMimeType("text/plain");
    }
    
    private boolean validateCredentials(String sessionURI, String credentials) {
        return credentials.length() > 0; // Should check that userid  stored as credential is still valid
    }
    
}
