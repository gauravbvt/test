package com.mindalliance.graph.accessor

import com.mindalliance.channels.nk.accessors.AbstractAccessor
import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper
import com.mindalliance.channels.nk.NetKernelCategory

/**
*
*/
class Flow extends AbstractAccessor {

    protected void source(INKFConvenienceHelper context) {
        use(NetKernelCategory) {
            def resp = context.subrequest("ffcpl:/resources/flow.svg", [mimeType: "image/svg+xml"]);
            context.respond(resp, "image/svg+xml", true);
        }
    }



}