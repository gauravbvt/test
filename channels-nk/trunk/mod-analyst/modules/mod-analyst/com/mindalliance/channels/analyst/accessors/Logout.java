// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.analyst.accessors;

import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper;
import org.ten60.netkernel.layer1.nkf.impl.NKFAccessorImpl;

import com.mindalliance.channels.nk.Session;
import com.mindalliance.channels.nk.ContextSupport;

public class Logout extends NKFAccessorImpl {
    
    public Logout() {
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
        session.deleteToken( "credentials" );
        // Show logout page
        ctx.subRequest("ffcpl:/analyst/view/logout").
            respond().
            setMimeType("text/html");
    }

}
