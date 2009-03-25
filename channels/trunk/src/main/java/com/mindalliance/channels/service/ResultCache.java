package com.mindalliance.channels.service;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;

/**
 * Generic method result cache.
 */
public class ResultCache {

    private Logger log = LoggerFactory.getLogger( getClass() );

    /** The manager for the internal cache. */
    private CacheManager cacheManager;

    /** The cache key, from the point of view of the manager. */
    private String cacheKey;

    /** The actual cache, lazy-inited. */
    private Cache cache;

    public ResultCache() {
    }

    /**
     * Cache the result of a method invocation.
     * @param invocation the invocation, used as key for the caching
     * @param result the result.
     */
    public void cache( MethodInvocation invocation, Object result ) {
        String key = getKey( invocation );
        if ( log.isTraceEnabled() )
            log.trace( MessageFormat.format( "Caching result of {0}", key ) );
        getCache().put( new Element( key, result ) );
    }

    /**
     * Return a cached value for an invocation.
     * @param invocation the invocation
     * @return null when no previous value was found
     */
    public Element getCached( MethodInvocation invocation ) {
        String key = getKey( invocation );
        Element element = getCache().get( key );
        if ( log.isTraceEnabled() && element != null )
            log.trace( MessageFormat.format( "Returning cached value for {0}", key ) );
        return element;
    }

    /**
     * Forget the result of one invocation.
     * @param invocation the invocation
     */
    public void forget( MethodInvocation invocation ) {
        getCache().remove( getKey( invocation ) );
    }

    /**
     * Forget all cached results.
     */
    public void forgetAll() {
        log.debug( "Clearing cache" );
        getCache().removeAll();
    }

    /**
     * Clean-up and get rid of the cache.
     */
    public synchronized void destroy() {
        cacheManager.removeCache( cacheKey );
        cache = null;
    }

    private synchronized Cache getCache() {
        if ( cache == null ) {
            cacheManager.addCache( cacheKey );
            cache = cacheManager.getCache( cacheKey );
        }
        return cache;
    }

    private static String getKey( MethodInvocation methodInvocation ) {
        String targetMethodName = methodInvocation.getMethod().getName();
        Object[] methodArgs = methodInvocation.getArguments();

        if ( methodArgs != null ) {
            StringBuilder key = new StringBuilder( targetMethodName );
            key.append( '(' );
            for ( int i = 0; i < methodArgs.length; i++ ) {
                if ( i != 0 )
                    key.append( ',' );
                key.append( String.valueOf( methodArgs[i] ) );
            }
            key.append( ')' );

            return key.toString();
        }
        else
            return targetMethodName;
    }

    public synchronized String getCacheKey() {
        return cacheKey;
    }

    public synchronized void setCacheKey( String cacheKey ) {
        this.cacheKey = cacheKey;
    }

    public synchronized CacheManager getCacheManager() {
        return cacheManager;
    }

    public synchronized void setCacheManager( CacheManager cacheManager ) {
        this.cacheManager = cacheManager;
    }
}
