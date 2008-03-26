package com.mindalliance.graph.accessor

import com.mindalliance.channels.nk.accessors.AbstractAccessor
import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper
import com.mindalliance.channels.nk.NetKernelCategory
import com.mindalliance.channels.graph.GraphVizRenderer
import com.ten60.netkernel.urii.aspect.StringAspect

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
            def source = context.sourceString("this:param:source");
            def mimeType = context.sourceString("this:param:format");
            def renderer = new GraphVizRenderer(source)
            def output = new StringWriter()
            renderer.render(output, typeMap[mimeType])
            context.respond(new StringAspect(output.toString()), mimeType, true);
        }
    }
}