package com.mindalliance.matcher.accessor

import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper as Context

import com.mindalliance.channels.nk.accessors.AbstractAccessor
import com.mindalliance.channels.nk.NetKernelCategory
import org.ten60.netkernel.layer1.nkf.INKFResponse

/**
 * Wrapping of the standard httpGet that systematically caches
 * regardless of page expiry.
 */
class HttpClient  extends AbstractAccessor {

    /** Expiration time (in milliseconds) for web pages */
    static final long CachedTime = 86400000L

    void source(Context ctx) {
        use(NetKernelCategory) {
//            System.out.println( ctx.request.URI )
            INKFResponse r = ctx.respond(
                ctx.subrequest( "active:httpGet",
                    [ url: "this:param:url"
                    ] ),
                "text/html", false)
            r.setExpiryPeriod( CachedTime )
            r.setCacheable()
        }
    }
}