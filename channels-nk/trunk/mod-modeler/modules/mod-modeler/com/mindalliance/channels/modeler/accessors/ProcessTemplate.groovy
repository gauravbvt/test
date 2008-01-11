package com.mindalliance.channels.modeler.accessors

import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper as Context

import com.mindalliance.channels.nk.accessors.AbstractAccessor
import com.ten60.netkernel.urii.aspect.IAspectString
import com.mindalliance.channels.nk.NetKernelCategory
/**
*
*/
class ProcessTemplate extends AbstractAccessor {
    void source(Context ctx) {
        use(NetKernelCategory) {
            String template = null
            String uri = ctx.template
            def test = ctx.params
            if (uri.startsWith("xrl:")) {
                // Obtain template via XRL, passing in links and param
                def args = ["uri": uri]
                ctx.args.each { args += [(it): ctx.request.getArgumentIfExists(it)]}
                template = ctx.transrept("active:source", IAspectString, args).getString()
            }
            else {
                template = ctx.sourceString(uri);
            }
            // Substitute in parameters passed in NVP.
               Map params = ctx.params.map
               for (entry in params) {
                   // param referenced by name
                    template = template.replaceAll("~~${entry.key}~~", entry.value);
                }
            println(template)
            // Make response
            ctx.respond(string(template), "text/xml", true)
        }
    }
}