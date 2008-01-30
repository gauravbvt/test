package com.mindalliance.matcher.accessor

import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper as Context
import groovy.xml.MarkupBuilder
import com.mindalliance.channels.nk.NetKernelCategory
import com.mindalliance.channels.nk.accessors.AbstractAccessor
import groovy.util.slurpersupport.GPathResult
import org.ten60.netkernel.layer1.nkf.INKFRequestReadOnly

/**
 * Basic semantic proximity matching.
 * TODO explain how its done
 */
class MatcherAccessor extends AbstractAccessor {

    final static String DefaultConfigUrl = "ffcpl:/etc/MatcherConfig.xml"

    static String getConfigUri( INKFRequestReadOnly request ) {
        return request.argumentExists( "config" ) ?
                "this:param:config" : DefaultConfigUrl
    }


    private GPathResult getSignature(
            Context ctx, String paramUri, String configUri) {

        return ctx.getXml(
                ctx.subrequest("active:signer",
                        [text: "this:param:$paramUri",
                                config: configUri
                        ])
                )
    }

    /**
     * Convert an xml signature to an index of topics and value
     * @param xml the parsed xml
     * @return signed values indexed by topics
     */
    private Map fromXml(GPathResult xml) {
        Map result = new HashMap()
        xml.topic.each {
            result[it.@name.toString()] = Double.valueOf(it.@value.toString())
        }
        return result
    }

    /**
     * Produce a match value based on two xml signatures.
     * @param text the base text signature
     * @param target the target text signature
     * @return Pearson correlation between the 2 signatures
     */
    double doMatch(GPathResult text, GPathResult target) {
        return correlate( fromXml( text ), fromXml( target ) )
    }

    /**
     * Produce a match value based on two vector.
     * @param x double values, indexed by a string
     * @param y double values, indexed by a string
     * @return Pearson correlation between the 2 vectors
     */
    static double correlate( Map x, Map y ) {

        double xBar = 0.0
        x.each {key, value ->
            xBar += value
            if (!y.containsKey(key))
                y[key] = 0.0
        }

        double yBar = 0.0
        y.each {key, value ->
            yBar += value
        }

        xBar /= x.size()
        yBar /= y.size()

        double prod = 0.0
        double x2 = 0.0
        double y2 = 0.0
        y.each {key, yVal ->
            double yDiff = yVal - xBar
            double xDiff = (x[key] ?: 0.0) - xBar
            prod += xDiff * yDiff
            x2 += xDiff * xDiff
            y2 += yDiff * yDiff
        }

        return prod / Math.sqrt(x2 * y2)
    }

    /**
     * Produce the match values for some text given a list of
     * target texts.
     */
    void source(Context ctx) {
        use(NetKernelCategory) {

            INKFRequestReadOnly request = ctx.request
            String configUri = getConfigUri( request )
            GPathResult sText = getSignature( ctx, "text", configUri )

            def targets = request.arguments
                    .findAll {it.startsWith("target")}

            Writer writer = new StringWriter()
            new MarkupBuilder(writer).matches(
                    text: ctx.sourceString("this:param:text").replaceAll("_", " "),
                    source: ctx.sourceXML(configUri).source) {
                targets.each {
                    match(
                            target: it,
                            value: doMatch(sText, getSignature( ctx, it, configUri )))
                }
            }

            ctx.respond(string(writer.toString()), "text/xml", false)
        }
    }
}