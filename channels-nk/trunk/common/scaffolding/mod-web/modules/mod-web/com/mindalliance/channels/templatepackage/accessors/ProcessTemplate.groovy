package com.mindalliance.channels.@templatepackage@.accessors

import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper as Context

import com.mindalliance.channels.nk.accessor.AbstractAccessor
import com.ten60.netkernel.urii.aspect.IAspectString
import com.mindalliance.channels.nk.NetKernelCategory
/**
*
*/
class ProcessTemplate extends AbstractAccessor {
    void source(Context ctx) {
        use(NetKernelCategory) {
            String template = null;
            String uri = ctx.template
            if (uri.startsWith("xrl:")) {
                // Obtain template via XRL, passing in links and param
                String links = ctx.operator
                template = ctx.transrept("active:source", IAspectString,
                        ["uri": uri,
                                "operator": links, // Exception if null
                                "param": ctx.params]).getString();
            }
            else {
                template = ctx.sourceString(uri);
            }
            // Substitute in parameters
            Map params = ctx.params.map
            for (entry in params) {
                template = template.replaceAll("~~" + entry.key + "~~", entry.value);
            }

            println(template)
            // Make response
            ctx.respond(string(template), "text/xml", true)
        }
    }
}