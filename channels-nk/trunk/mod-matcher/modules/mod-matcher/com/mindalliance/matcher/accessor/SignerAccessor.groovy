package com.mindalliance.matcher.accessor

import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper as Context

import com.mindalliance.channels.nk.accessors.AbstractAccessor
import com.mindalliance.channels.nk.NetKernelCategory
import groovy.xml.MarkupBuilder
import groovy.util.slurpersupport.GPathResult
import java.util.regex.Matcher

/**
 * This is a loose implementation of ...
 * TODO add link to pdf file
 */
class SignerAccessor extends AbstractAccessor {

    /** Impact weight half-life.
     * Index of the raw topic for which the impact weight is exactly half as much
     * as the weight of top-ranked result topic.
     * When large, all ranked topic are equivalent (ie, the ordering of the
     * result is ignored).
     */
    static final double halfLife = Double.MAX_VALUE

    /**
     * Influence of the number of children of a node ( >= 0 ).
     * TODO fix this description
     */
    static final double childCountWeight = 0

    /**
     * Effect of children scores on a node's score ( >= 0 ).
     * TODO fix this description
     */
    static final double childScoreWeight = 1

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
     private Map sign( GPathResult rawTopics ) {

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
         final Map μRaw = new TreeMap()

         rawTopics.topic.eachWithIndex { t,i ->
             String topic = t.@name
             if ( topic.endsWith( "/" ) )
                topic = topic.substring( 0, topic.size()-1 )
             addChild( children, topic )
             μRaw[ topic ] = halfLife == Double.MAX_VALUE ?
                 1.0 : Math.pow(2.0d, -i / halfLife)
         }

         double n = 0.0

         // Compute developped topics scores
         final Map μ = new TreeMap()
         children.each { String topic, int kidCount ->
             // Compute μ[ topic ] =
             //     μRaw[ topic ] + sum(kids) / ( γ + δ * kidCount )
             //     ( (1-y) * μRaw[ topic ] + y * ( sum / δ * kidCount ) )

             // The original intent of the article is closer to:
             //     μRaw[ topic ] + sum(directChildren(t)) / (γ + δ * Math.log(kidCount + 1))

             // Sum of children is already in μ[ topic ] because of map ordering
             double factor = childScoreWeight + childCountWeight * kidCount
             if ( kidCount != 0 && factor != 0.0 ) {
                 μ[ topic ] /= factor
             }
             else
                 μ[ topic ] = 0.0

             μ[ topic ] += μRaw[topic] ?: 0.0
             n += μ[ topic ]
             String parent = parent( topic )
             if ( parent != null )  {
                if ( μ.containsKey( parent ) )
                    μ[ parent ] += μ[ topic ]
                else
                    μ[ parent ] = μ[ topic ]
             }
         }

         // Normalize
         μ.each { key,val -> μ[ key ] = val / n  }
         μ.remove( "" )

         return μ
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

   void source( Context ctx ) {
        use(NetKernelCategory) {

            def writer = new StringWriter()
            new MarkupBuilder(writer).signature(text: ctx.request.text) {
                sign(ctx.sourceXML(ctx.request.topicsUrl)).each {
                    topic(name: it.key, value: it.value)
                }
            }

            ctx.respond( string( writer.toString() ), "text/xml", false )
        }
    }

}