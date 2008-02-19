package com.mindalliance.channels.matcher.tests

import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper as Context
import com.mindalliance.channels.nk.NetKernelCategory
import groovy.xml.MarkupBuilder

/**
 * Test of the ctx.match( text, text ) wrapper.
 */
class CategoryTest {

    Context context

    void single() {
        use( NetKernelCategory ) {
            double value = context.match( "software development", "Unit tests" )

            final StringWriter writer = new StringWriter()
            new MarkupBuilder( writer ).result( value: value )
            context.respond( string( writer.toString() ), "text/xml", false )
        }
    }

    void multiple() {
        use( NetKernelCategory ) {
            List<String> values = context.relevanceSort(
                    "software development",
                    [ "Unit tests", "Java", "junit" ] )

            final StringWriter writer = new StringWriter()
            new MarkupBuilder( writer ).result() {
                values.each { value( it ) }
            }
            context.respond( string( writer.toString() ), "text/xml", false )
        }
    }

}