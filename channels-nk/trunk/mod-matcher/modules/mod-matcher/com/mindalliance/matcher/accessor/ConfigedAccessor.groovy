package com.mindalliance.matcher.accessor

import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper as Context
import com.mindalliance.channels.nk.NetKernelCategory
import com.mindalliance.channels.nk.accessors.AbstractAccessor
import groovy.util.slurpersupport.GPathResult
import org.ten60.netkernel.layer1.nkf.INKFRequestReadOnly

/**
 * Utility method for accessors driven by either MatcherConfig.xml or
 * 'config' parameter.
 */
abstract class ConfigedAccessor extends AbstractAccessor {

    final static String DefaultConfigUrl = "ffcpl:/etc/MatcherConfig.xml"

    static String getConfigUri( INKFRequestReadOnly request ) {
        return request.argumentExists( "config" ) ?
                "this:param:config" : DefaultConfigUrl
    }

    GPathResult getConfig( Context ctx ) {
        GPathResult result = null
        use ( NetKernelCategory ) {
            result = ctx.sourceXML( getConfigUri( ctx.request ) )
        }

        return result
    }

    String getText( Context ctx ) {
        String result = null
        use ( NetKernelCategory ) {
            result = ctx.sourceString( "this:param:text" )
                        .replaceAll( "_", " " )
        }

        return result
    }

    String encode( String str ) {
        return URLEncoder.encode( str ).replaceAll( "\\+", "%20" )
    }
}