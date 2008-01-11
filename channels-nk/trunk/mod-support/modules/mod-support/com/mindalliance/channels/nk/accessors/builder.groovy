package com.mindalliance.channels.nk.accessors

import com.mindalliance.channels.nk.NetKernelCategory
import com.mindalliance.channels.nk.accessors.AbstractAccessor
import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper as Context

/**
*    active:support_builder+operand@builderString[+paramName@paramValue]*
*   In the builder string, the builder is referenced as _.builder
*   and all other parameters as _.nameOfParameter
*/
class Builder extends AbstractAccessor {
    void source(Context ctx) {
        use(NetKernelCategory) {
            String builderString = ctx.sourceString(ctx.operand);
            StringWriter writer = new StringWriter()
            groovy.xml.MarkupBuilder markupBuilder = new groovy.xml.MarkupBuilder(writer)
            Map args = ['builder': markupBuilder]
            ctx.args.each {
                if (it != 'operand')
                    args += [(it): ctx.sourceString(ctx."${it}")]
            }
            Eval.me('_', args, builderString)
            String markup = writer.toString()
            ctx.respond(string(markup), 'text/xml', true)
        }
    }
}