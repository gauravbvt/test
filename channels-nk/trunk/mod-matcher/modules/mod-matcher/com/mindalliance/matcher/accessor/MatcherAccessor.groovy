package com.mindalliance.matcher.accessor

import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper as Context
import groovy.xml.MarkupBuilder
import com.mindalliance.channels.nk.NetKernelCategory
import com.mindalliance.channels.nk.accessors.AbstractAccessor
import groovy.util.slurpersupport.GPathResult

/**
 * Created by IntelliJ IDEA.
 * User: denis
 * Date: 2008-01-16
 * Time: 13:19:22
 * To change this template use File | Settings | File Templates.
 */
class MatcherAccessor extends AbstractAccessor {

    Map fromXml( GPathResult xml ) {
        Map result = new HashMap()
        xml.topic.each {
            result[ it.@name.toString() ] = Double.valueOf( it.@value.toString() )
        }
        return result
    }

    double doMatch( Context ctx, String source, String text, String target ) {
        return doMatch(
                ctx.sourceXML(
                    "ffcpl:/matcher/sign+text@${recode( text )}+source@$source" ),
                ctx.sourceXML(
                    "ffcpl:/matcher/sign+text@${recode( target )}+source@$source" )
            )
    }

    double doMatch( Context ctx, String source, GPathResult text, String target ) {
        return doMatch(
                text,
                ctx.sourceXML(
                    "ffcpl:/matcher/sign+text@${recode( target )}+source@$source" )
            )
    }


    double doMatch( GPathResult text, GPathResult target ) {
        Map x = fromXml( text )
        Map y = fromXml( target )

        double xBar = 0.0
        x.each { key, value ->
            xBar += value
            if ( ! y.containsKey( key ) )
                y[ key ] = 0.0
        }

        double yBar = 0.0
        y.each { key, value ->
            yBar += value
        }

        xBar /= x.size()
        yBar /= y.size()

        double prod = 0.0
        double x2 = 0.0
        double y2 = 0.0
        y.each { key, yVal ->
            double yDiff = yVal - xBar
            double xDiff = ( x[ key ] ?: 0.0 ) - xBar
            prod += xDiff * yDiff
            x2 += xDiff * xDiff
            y2 += yDiff * yDiff
        }

        return prod / Math.sqrt( x2 * y2 )
    }

    private String recode( String arg ) {
        return URLEncoder.encode(arg, "utf-8").replaceAll(/\+/, "%20")
    }

    void source( Context ctx ) {
         use( NetKernelCategory ) {
             def request = ctx.request
             String text = request.text
             String source = request.source
             def targets = request.arguments
                     .findAll { it.startsWith("target") }
                     .collect { request.get(it) }

             GPathResult sText = ctx.sourceXML(
                     "ffcpl:/matcher/sign+text@${recode( text )}+source@$source" )

             Writer writer = new StringWriter()
             new MarkupBuilder(writer).matches(text: text, source: source) {
                 targets.each {
                     GPathResult sTarget = ctx.sourceXML(
                         "ffcpl:/matcher/sign+text@${recode( it )}+source@$source" )
                     match( target: it, value: doMatch( sText, sTarget ) )
                 }
             }

             ctx.respond(string(writer.toString()), "text/xml", false)
         }
     }

}