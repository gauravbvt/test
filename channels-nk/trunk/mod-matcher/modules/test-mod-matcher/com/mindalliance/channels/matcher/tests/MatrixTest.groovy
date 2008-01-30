package com.mindalliance.channels.matcher.tests

import com.mindalliance.channels.nk.NetKernelCategory
import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper as Context
import groovy.xml.MarkupBuilder
import groovy.util.slurpersupport.GPathResult

class MatrixTest {

    static final String Matches = "ffcpl:/test/resources/matches.xml"

    Context context
    String configUri = "ffcpl:/etc/MatcherConfig.xml"

    MatrixTest( Context ctx ) {
        context = ctx
    }

    String encode( String text ) {
        return URLEncoder.encode( text ).replaceAll( "\\+", "%20" )
    }

    double matchValue( String text, String target ) {
        return Double.valueOf(
            context.getXml(
                context.subrequest("active:matcher",
                    [ text: "data:,${ encode( text ) }",
                      target: "data:,${ encode( target ) }",
                      config: configUri
                    ])
                ).match.@value.toString()
        )
    }

    void outputMatrix() {

        use( NetKernelCategory ) {

            GPathResult tests = context.sourceXML( Matches )
            Map results = new HashMap()
            Map expectations = new HashMap()

            tests.match.each {
                final String text = it.text
                final String target = it.target
                if ( target.length() > 0 ) {
                    final String[] key = [ text, target ]

                    expectations[ key ] = it.expect
                    results[ key ] = matchValue( text, target )
                }
            }

            Writer w = new StringWriter()
            new MarkupBuilder( w ).results() {
                results.each { key, value ->
                    match( text: key[0], target: key[1], value: value )
                }
            }

            context.respond( string( w.toString() ), "text/xml", false )
        }
    }

}