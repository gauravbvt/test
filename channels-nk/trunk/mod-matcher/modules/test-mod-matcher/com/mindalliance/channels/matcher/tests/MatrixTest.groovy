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

    double matchValue( String text, String target, String config ) {
        double result = 0.0
        use( NetKernelCategory ) {
            result = Double.valueOf(
                context.getXml(
                    context.subrequest("active:matcher",
                        [ text: data( text ),
                          target: data( target ),
                          config: config
                        ])
                    ).match.@value.toString() )
        }

        return result
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

    static double valuate( String id ) {
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

    /** Cut and pasted from MatcherAccessor... */
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

        if ( x2 == 0 || y2 == 0 )
            return 0.0
        else
            return prod / Math.sqrt(x2 * y2)
    }

    String makeUri( String src, double hl, double c, double s ) {
        Writer w = new StringWriter()
        def b = new MarkupBuilder( w ).config() {
            source( src )
            halfLife( hl )
            childCountWeight( c )
            childScoreWeight( s )
        }

        return "data:text/xml," + encode( w.toString() )
    }

    double rate( String source, double halfLife, double counts, double scores ) {
        Map rows = getTests()
        Map ideal = new TreeMap()
        Map actual = new TreeMap()
        String tmpConfig = makeUri( source, halfLife, counts, scores )

        rows.each { key, r ->
            r.keySet().each {
                if ( it > key ) {
                    final double val = matchValue( key, it, tmpConfig )
                    final String expectation = rows[key]?.get(it) == null ? "normal" : rows[key][it]
                    if ( expectation != "normal" ) {
                        final String k = key + it
                        ideal[ k ] = valuate( expectation )
                        actual[ k ] = val // trim( val )
                    }
                }
            }
        }
        return correlate( actual, ideal )
    }

    /** Find some suitable parameters the hard way... */
    void find( String src ) {
        use( NetKernelCategory ) {
            def bestParms = [ -1, 0, 0 ]
            double bestScore = rate( src, -1.0d, 0.0d, 0.0d )

            for ( double halfLife = 100.0 ; halfLife > 0.0 ; halfLife-=10 ) {
                for ( double counts = 0.0 ; counts <= 10.0 ; counts++ ) {
                    for ( double scores = 0.0 ; scores <= 10.0 ; scores++ ) {
                        double score = rate( src, halfLife, counts, scores )
                        if ( score > bestScore ) {
                            bestParms = [ halfLife, counts, scores ]
                            bestScore = score
                        }
                    }
                }
            }

            Writer w = new StringWriter()
            w.write( '<?xml version="1.0" encoding="UTF-8"?>\r\n' )
            def builder = new MarkupBuilder( w ).config() {
                source( src )
                halfLife( bestParms[0] )
                childCountWeight( bestParms[1] )
                childScoreWeight( bestParms[2] )
            }

            context.respond( string( w.toString() ), "text/xml", false )
        }

    }

    Map getTests() {
        Map rows = new TreeMap()
        use( NetKernelCategory ) {
            GPathResult tests = context.sourceXML( Matches )

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
        }
        return rows
    }

    void outputMatrix() {

        use( NetKernelCategory ) {

            GPathResult config = context.sourceXML( configUri )
            Map rows = getTests()
            Map ideal = new TreeMap()
            Map actual = new TreeMap()

            Writer w = new StringWriter()
            w.write( '<?xml version="1.0" encoding="UTF-8"?>\r\n' )
            new MarkupBuilder( w ).results(
                    source: config.source,
                    halfLife: config.halfLife,
                    childCount: config.childCountWeight,
                    childScores: config.childScoreWeight,
                    ) {
                rows.each { key, r ->
                    row( text: key ) {
                        rows.keySet().asList().reverseEach {
                            if ( it > key ) {
                                final double val = matchValue( key, it, configUri )
                                final String expectation = rows[key]?.get(it) == null ? "normal" : rows[key][it]
                                if ( expectation != "normal" ) {
                                    final String k = key + it
                                    ideal[ k ] = valuate( expectation )
                                    actual[ k ] = val // trim( val )
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

                score( value: correlate( actual, ideal ) )
            }

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