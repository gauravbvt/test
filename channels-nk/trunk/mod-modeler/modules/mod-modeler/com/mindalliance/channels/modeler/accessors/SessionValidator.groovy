package com.mindalliance.channels.modeler.accessors

import com.mindalliance.channels.nk.accessor.AbstractAccessor
import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper as Context
import org.ten60.netkernel.layer1.representation.BooleanAspect

import com.mindalliance.channels.nk.Session
import com.mindalliance.channels.nk.NetKernelCategory
/**
*
*/
class SessionValidator  extends AbstractAccessor {

    void source( Context ctx ) {
        use (NetKernelCategory) {
            Session session = ctx.session;
            boolean isValid;
            if(session.'credentials?') {
                String credentials = session.credentials
                // Validate Credentials
                isValid = validateCredentials( session.sessionURI, credentials );
            }
            else {
                // No credentials exist for this session
                isValid = false;
            }
            ctx.respond(new BooleanAspect(isValid),"text/plain", false);
        }
    }

    private boolean validateCredentials(String sessionURI, String credentials) {
        return credentials.length() > 0; // Should check that userid  stored as credential is still valid
    }
}