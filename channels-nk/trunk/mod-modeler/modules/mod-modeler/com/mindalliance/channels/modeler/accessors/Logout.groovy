package com.mindalliance.channels.modeler.accessors

import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper as Context;
import com.mindalliance.channels.nk.accessors.AbstractAccessor;
import com.mindalliance.channels.nk.NetKernelCategory

/**
*
*/
class Logout extends AbstractAccessor {
     void source( Context ctx ) {
        use (NetKernelCategory) {
            def session = ctx.session;
            session.credentials = null
            // Show logout page
            ctx.respond("ffcpl:/analyst/view/logout","text/html")
        }
    }
}