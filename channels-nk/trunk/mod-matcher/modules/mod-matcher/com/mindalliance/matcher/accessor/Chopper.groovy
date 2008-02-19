package com.mindalliance.matcher.accessor

import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper as Context
import groovy.util.slurpersupport.GPathResult
import com.mindalliance.channels.nk.NetKernelCategory
import com.mindalliance.matcher.javatools.PlingStemmer

/**
 * Decompose a piece of text into smaller, semantically signable chunks.
 */
class Chopper extends ConfigedAccessor {

    Set<String> stopWords

    List<String> uniqueWords( final String text ) {
        Set<String> words = new TreeSet<String>()

        text.toLowerCase()
            .split( /[^\w\-]/ )
            .each {
                if ( ! stopWords.contains( it ) )
                    words.add( PlingStemmer.stem( it ) )
            }

        return words.asList()
    }

    List<String> split( final String text ) {
        List<String> result = new ArrayList<String>()
        text.split( /[\.;!\?]\s*/ ) .each {
                result.add( it )
            }

        return result
    }

    /**
     * Cut a potentially large text in smaller chunks
     */
    void source(Context ctx) {
        use(NetKernelCategory) {

            if ( stopWords == null ) {
                stopWords = new HashSet<String>()
                String sw = ctx.sourceString( "ffcpl:/resources/stopwords.txt" )
                sw.split( /\n/ ).each { stopWords.add( it ) }
                stopWords.add( "" )
            }

            GPathResult config = getConfig( ctx )
            String text = getText( ctx )

            List<String> chunks = uniqueWords( text )
            String configUri = ctx.request.config
            if ( configUri == null )
                configUri = ""
            else
                configUri = "+config@$configUri"

            Map args = new HashMap()
            chunks.eachWithIndex { t, i ->
                args[ "sign$i" ] = "active:signer+text@${ data(t) }$configUri"
            }
            args[ "expired" ] = false
            args[ "cachable" ] = true
            args[ "mimeType" ] = "text/xml"

            ctx.subrequest( "active:combiner", args )


//            Writer writer = new StringWriter()
//            final MarkupBuilder builder = new MarkupBuilder(writer)
//            builder.chopped() {
//                chunks.each { String s ->
//                    chunk( text: s ) {
//                    }
//                }
//            }

//            INKFResponse r = ctx.respond(string(writer.toString()), "text/xml", false)
//            r.setCacheable()
        }
    }

}