package com.mindalliance.channels.matcher.tests

import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper as Context

import com.mindalliance.channels.nk.NetKernelCategory
import groovy.util.slurpersupport.GPathResult
import groovy.xml.MarkupBuilder
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource
import javax.xml.transform.TransformerFactory

class MatrixTest {

    static final String Matches = "ffcpl:/test/resources/matches.xml"
    static final String Xsl = "ffcpl:/test/resources/matrix.xsl"
    static final double HI = 0.60
    static final double MED = 0.30
    static final double LO = 0.15
    static final double NONE = -0.15
    static final double NEG = -1.0

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

    boolean check( double value, String expectation ) {
       switch ( expectation ) {
           case "hi":    return value >= HI
           case "med":   return value >= MED && value < HI
           case "lo":    return value >= LO && value < MED
           case "none":  return value >= NONE && value < LO
           case "neg":   return value < NONE
           default: return true
       }
    }

    double trim( double value ) {
        if ( value >= HI )
            return HI
        else if ( value >= MED )
            return MED
        else if ( value >= LO )
            return LO
        else if ( value >= NONE )
            return NONE
        else
            return NEG
    }

    double valuate( String id ) {
        switch ( id ) {
            case "hi":    return HI
            case "med":   return MED
            case "lo":    return LO
            case "none":  return NONE
            default:
            // case "neg":
                return NEG
        }
    }

    void outputMatrix() {

        use( NetKernelCategory ) {

            GPathResult tests = context.sourceXML( Matches )
            GPathResult config = context.sourceXML( configUri )
            Map rows = new TreeMap()

            tests.match.each {
                final String text = it.text
                if ( ! rows.containsKey( text ) )
                    rows[ text ] = new HashMap()
                final String target = it.target
                if ( target.length() > 0 ) {
                    if ( ! rows.containsKey( target ) )
                        rows[ target ] = new HashMap()
                    if ( text < target )
                        rows[ text ][ target ] = it.expect
                    else
                        rows[ target ][ text ] = it.expect
                }
            }

            Map ideal = new TreeMap()
            Map actual = new TreeMap()
            double score = 1.0

            Writer w = new StringWriter()
            w.write( '<?xml version="1.0" encoding="UTF-8"?>\r\n' )
            new MarkupBuilder( w ).results(
                    source: config.source,
                    halfLife: config.halfLife,
                    childCount: config.childCountWeight,
                    childScores: config.childScoreWeight,
                    score: score ) {
                rows.each { key, r ->
                    row( text: key ) {
                        rows.keySet().asList().reverseEach {
                            if ( it > key ) {
                                final double val = matchValue( key, it )
                                final String expectation = rows[key]?.get(it) == null ? "normal" : rows[key][it]
                                if ( expectation != "normal" ) {
                                    final String k = key + it
                                    ideal[ k ] = valuate( expectation )
                                    actual[ k ] = trim( val )
                                }
                                cell(
                                    text:it,
                                    value: sprintf( "%1.4f", val ),
                                    pass: check( val, expectation ),
                                    expected: expectation
                                    )
                            }
                        }
                    }
                }
            }

//            score = MatcherAccessor.correlate( actual, ideal )

            // Format results to a legible html page
            def out = new StringWriter()
            def factory = TransformerFactory.newInstance()
            def transformer = factory.newTransformer(
                    new StreamSource( new StringReader( context.sourceString( Xsl ) )))
            transformer.transform(
                    new StreamSource(
                            new StringReader(w.toString())),
                            new StreamResult(out))

            context.respond( string( out.toString() ), "text/html", false )
        }
    }

}