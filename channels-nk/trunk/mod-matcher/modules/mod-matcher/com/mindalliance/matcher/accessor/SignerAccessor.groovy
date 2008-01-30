package com.mindalliance.matcher.accessor

import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper as Context

import com.mindalliance.channels.nk.accessors.AbstractAccessor
import com.mindalliance.channels.nk.NetKernelCategory
import groovy.xml.MarkupBuilder
import groovy.util.slurpersupport.GPathResult
import java.util.regex.Matcher
import org.ten60.netkernel.layer1.nkf.INKFRequestReadOnly

/**
 * This is a loose implementation of ...
 * TODO add link to pdf file
 */
class SignerAccessor extends AbstractAccessor {

    /**
     * Return the parent topic of a topic.
     * @return null when the topic if a child of root
     */
    private String parent( String topic ) {
        Matcher m = topic =~ /(.*)\/[^\/]*/
        return m.matches() ? m.group(1) : null
    }

    /**
     * Add a raw topic to the developped list.
     * The parent's count is incremented by one
     * (and the parent is added/developped if not already)
     *
     * @param childCount the developped count map
     * @param topic the topic to add
     */
    private Map addChild( Map counts, String topic ) {
        int size = topic.size()
        if ( size > 0 && topic[ size-1 ] == '/' )
            topic = topic.substring( 0, size-1 )
        if ( !counts.containsKey( topic ) ) {
            counts[ topic ] = 0
            String parent = parent( topic )
            if ( parent != null ) {
                addChild( counts, parent )
                counts[ parent ]++
            }
        }
    }

    /**
     * Generate a signature starting from raw topics.
     * @param rawTopics the input XML in the form:
     * <p><pre>&lt;topics&gt;
     *   &lt;topic name="..."/&gt;...</pre></p>
     * @return a map of the weight of the developped topics
     */
    Map sign(
            GPathResult rawTopics,
            double halfLife, double childCountWeight, double childScoreWeight ) {

         // Number of children of each developped node
         Map children = new TreeMap([
                 compare: {a, b ->
                     int aDepth = a.count("/")
                     int bDepth = b.count("/")
                     return aDepth == bDepth ? a.compareTo(b) : bDepth.compareTo(aDepth)
                 },
                 equals: { a, b -> a == b }
            ] as Comparator
            )

         // Basic scores of raw topics
         final Map rawScores = new TreeMap()

         rawTopics.topic.eachWithIndex { t,i ->
             String topic = t.@name
             if ( topic.endsWith( "/" ) )
                topic = topic.substring( 0, topic.size()-1 )
             addChild( children, topic )
             rawScores[ topic ] = halfLife > 0 ?
                 1.0 : Math.pow( 2.0d, -i / halfLife )
         }

         double n = 0.0

         // Compute developped topics scores
         final Map scores = new TreeMap()
         children.each { String topic, int kidCount ->
             // Compute μ[ topic ] =
             //     μRaw[ topic ] + sum(kids) / ( γ + δ * kidCount )
             //     ( (1-y) * μRaw[ topic ] + y * ( sum / δ * kidCount ) )

             // The original intent of the article is closer to:
             //     μRaw[ topic ] + sum(directChildren(t)) / (γ + δ * Math.log(kidCount + 1))

             // Sum of children is already in μ[ topic ] because of map ordering
             double factor = childScoreWeight + childCountWeight * kidCount
             if ( kidCount != 0 && factor != 0.0 ) {
                 scores[ topic ] /= factor
             }
             else
                 scores[ topic ] = 0.0

             scores[ topic ] += rawScores[topic] ?: 0.0

             n += scores[ topic ]
             String parent = parent( topic )
             if ( parent != null )  {
                if ( scores.containsKey( parent ) )
                    scores[ parent ] += scores[ topic ]
                else
                    scores[ parent ] = scores[ topic ]
             }
         }

         // Normalize
         scores.each { key,val -> scores[ key ] = val / n  }
         scores.remove( "" )

         return scores
    }

    Map valueSort( final Map map ) {
       Map result = new TreeMap([
                 compare: {a, b ->
                     int aDepth = a.count("/")
                     int bDepth = b.count("/")
                     return ( (Double) map[ b ] ).compareTo( (Double) map[ a ] )
                 },
                 equals: { a, b -> a == b }
            ] as Comparator
            )

       map.each { k, v ->  result[ k ] = v }
       return result
    }

    private GPathResult getRawTopics( Context ctx, String source, String text ) {
        String sanitizedText = URLEncoder.encode( text ).replaceAll( "\\+", "%20" )
        return ctx.sourceXML( "$source-cooked:$sanitizedText" )
    }

    /**
     * Produce a signature for given text.
     */
    void source( Context ctx ) {
        use( NetKernelCategory ) {

            String text = ctx.sourceString("this:param:text").replaceAll("_", " ");

            INKFRequestReadOnly request = ctx.request
            GPathResult config = ctx.sourceXML(
                 MatcherAccessor.getConfigUri( request ) )

            String source = config.source
            double halfLife = Double.valueOf( config.halfLife.toString() )
            double childCountWeight = Double.valueOf( config.childCountWeight.toString() );
            double childScoreWeight = Double.valueOf( config.childScoreWeight.toString() );

            def writer = new StringWriter()
            new MarkupBuilder(writer).signature( text: text ) {
                sign(
                    getRawTopics( ctx, source, text ),
                    halfLife,
                    childCountWeight,
                    childScoreWeight ).each {

                    topic(name: it.key, value: it.value)
                }
            }

            ctx.respond( string( writer.toString() ), "text/xml", false )
        }
    }

}