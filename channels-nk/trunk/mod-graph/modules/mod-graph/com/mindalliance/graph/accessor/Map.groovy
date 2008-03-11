package com.mindalliance.graph.accessor

import com.mindalliance.channels.nk.accessors.AbstractAccessor
import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper
import com.mindalliance.channels.nk.NetKernelCategory

/**
*
*/
class Map extends AbstractAccessor {

    protected void source(INKFConvenienceHelper context) {
        use(NetKernelCategory) {
            def resp = context.subrequest("ffcpl:/resources/map.png", [mimeType: "image/png"]);
            context.respond(resp, "image/png", true);
        }
    }
}


