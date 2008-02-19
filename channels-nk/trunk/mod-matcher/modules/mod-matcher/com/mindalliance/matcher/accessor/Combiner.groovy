package com.mindalliance.matcher.accessor

import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper as Context
import groovy.xml.MarkupBuilder
import com.mindalliance.channels.nk.NetKernelCategory
import com.mindalliance.channels.nk.accessors.AbstractAccessor
import groovy.util.slurpersupport.GPathResult
import org.ten60.netkernel.layer1.nkf.INKFRequestReadOnly
import org.ten60.netkernel.layer1.nkf.INKFResponse

/**
 * Combines a bunch of signature by addition.
 */
class Combiner extends AbstractAccessor {

    String addSig( GPathResult sig, Map combined ) {
        String result = sig.@text

        sig.topic.each {
            String name = it.@name.toString()
            double value = Double.valueOf( it.@value.toString() )
            if ( combined.containsKey( name ) )
                combined[ name ] += value
            else
                combined[ name ] = value
        }

        return result
    }

    void source(Context ctx) {
        use(NetKernelCategory) {

            INKFRequestReadOnly request = ctx.request

            def signatures = request.arguments
                    .findAll { it.startsWith("sign") }

            Map result = new TreeMap()
            String text = ""
            signatures.each {
                GPathResult sig = ctx.sourceXML( "this:param:$it" )
                text = addSig( sig, result )
            }

            Writer writer = new StringWriter()
            new MarkupBuilder(writer).signature( text: text ) {
                result.each { k, v ->
                    topic( name: k, value: v )
                }
            }

            INKFResponse r = ctx.respond(string(writer.toString()), "text/xml", false)
            r.setCacheable()
            return r
        }
    }

}