/*
 * $Id: CacheFilter.java 90 2011-03-07 22:06:02Z samaxes $ 
 *
 * Copyright 2008 samaxes.com
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.samaxes.filter;

import com.samaxes.filter.util.CacheConfigParameter;
import com.samaxes.filter.util.Cacheability;
import com.samaxes.filter.util.HTTPCacheHeader;
import org.apache.log4j.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Filter responsible for browser caching.
 *
 * @author Samuel Santos
 * @version $Revision: 25 $
 */
public class CacheFilter implements Filter {

    private Cacheability cacheability;

    private boolean isStatic;

    private long seconds;

    private static final Logger LOGGER = Logger.getLogger( CacheFilter.class );

    /**
     * Place this filter into service.
     *
     * @param filterConfig the filter configuration object used by a servlet container to pass information to a filter
     * during initialization
     * @throws ServletException to inform the container to not place the filter into service
     */
    @Override
    public void init( FilterConfig filterConfig ) throws ServletException {
        cacheability = Boolean.valueOf( filterConfig.getInitParameter( CacheConfigParameter.PRIVATE.getName() ) ) ?
                       Cacheability.PRIVATE :
                       Cacheability.PUBLIC;
        isStatic = Boolean.valueOf( filterConfig.getInitParameter( CacheConfigParameter.STATIC.getName() ) );

        try {
            seconds = Long.valueOf( filterConfig.getInitParameter( CacheConfigParameter.EXPIRATION_TIME.getName() ) );
            
        } catch ( NumberFormatException ignored ) {
            throw new ServletException( "The initialization parameter " + CacheConfigParameter.EXPIRATION_TIME.getName()
                                            + " is missing for filter " + filterConfig.getFilterName() + '.' );
        }
    }

    /**
     * Set cache header directives.
     *
     * @param request provides data including parameter name and values, attributes, and an input stream
     * @param response assists a servlet in sending a response to the client
     * @param chain gives a view into the invocation chain of a filtered request
     */
    @Override
    public void doFilter( ServletRequest request, ServletResponse response, FilterChain chain )
        throws IOException, ServletException {
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        StringBuilder cacheControl = new StringBuilder( cacheability.getValue() ).append( ", max-age=" )
                                                                                 .append( seconds );

        if ( !isStatic )
            cacheControl.append( ", must-revalidate" );

        // Set cache directives
        httpServletResponse.setHeader( HTTPCacheHeader.CACHE_CONTROL.getName(), cacheControl.toString() );
        httpServletResponse.setDateHeader( HTTPCacheHeader.EXPIRES.getName(),
                                           System.currentTimeMillis() + seconds * 1000L );

        /*
         * By default, some servers (e.g. Tomcat) will set headers on any SSL content to deny caching. Setting the
         * Pragma header to null or to an empty string takes care of user-agents implementing HTTP 1.0.
         */
        if ( httpServletResponse.containsHeader( "Pragma" ) ) {
            LOGGER.debug( "found pragma header" );
            httpServletResponse.setHeader( HTTPCacheHeader.PRAGMA.getName(), "" );
        } else
            LOGGER.debug( "did not find pragma header" );

        chain.doFilter( request, response );
    }

    /**
     * Take this filter out of service.
     */
    @Override
    public void destroy() {
    }
}
