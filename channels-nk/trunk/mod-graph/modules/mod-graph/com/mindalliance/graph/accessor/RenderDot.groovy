package com.mindalliance.graph.accessor

import com.mindalliance.channels.nk.accessors.AbstractAccessor
import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper
import com.mindalliance.channels.nk.NetKernelCategory

/**
 * This accessor shells out to the graphviz 'dot' command.  It requires the graphviz package be in the running
 * netkernel path.
 */
class RenderDot extends AbstractAccessor {
    static typeMap = ["image/svg+xml" : "svg",
                      "image/svg" : "svg",
                      "image/png" : "png",
                      "image/jpg" : "jpeg",
                      "image/jpeg" : "jpeg",
                      "image/gif" : "gif",
                      "image/tiff" : "tiff",
                      "image/tga" : "tga",
                      "application/pdf" : "pdf"];

    protected void source(INKFConvenienceHelper context) {
        use(NetKernelCategory) {
            def source = context.source;
            def mimeType = context.sourceString("this:param:format");

            def resp = context.subrequest("active:exec",
                    [mimeType: mimeType,
                     command: "data:" + mimeType + ",dot%20-T" + typeMap[mimeType],
                     stdin: source]);
            context.respond(resp, mimeType, true);
        }
    }
}