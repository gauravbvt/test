package com.mindalliance.channels.core.query;

import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Identifiable;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;

/**
 * Generic method result cache.
 */
public class ResultCache {

    private static final Logger LOG = LoggerFactory.getLogger( ResultCache.class );

    /**
     * The manager for the internal cache.
     */
    private CacheManager cacheManager;

    /**
     * The cache key, from the point of view of the manager.
     */
    private String cacheKey;

    /**
     * The actual cache, lazy-inited.
     */
    private Cache cache;

    public ResultCache() {
    }

    /**
     * Cache the result of a method invocation.
     *
     * @param invocation the invocation, used as key for the caching
     * @param result     the result.
     */
    public void cache( MethodInvocation invocation, Object result ) {

        String contextUri = ChannelsUser.contextUri();
        LOG.trace(
                MessageFormat.format(
                        "Caching result {0} of {1} in {2}",
                        cache( getKey( invocation, contextUri ), result ),
                        getKey( invocation, contextUri ),
                        contextUri ) );
    }

    private synchronized int cache( String key, Object result ) {
        int count = getCache().getSize();

        cache.put(
                new Element(
                        key, result instanceof List ? Collections.unmodifiableList( (List<?>) result ) : result ) );

        return count;
    }

    /**
     * Return a cached value for an invocation.
     *
     * @param invocation the invocation
     * @return null when no previous value was found
     */
    public Element getCached( MethodInvocation invocation ) {
        String key = getKey( invocation, ChannelsUser.contextUri() );
        Element element = null;
        try {
            element = getCache().get( key );
        } catch ( NullPointerException e ) {
            LOG.error( "Reading key " + key, e );
        } catch ( IllegalStateException e ) {
            LOG.error( "Reading key " + key, e );
        } catch ( CacheException e ) {
            LOG.error( "Reading key " + key, e );
        }

        if ( element == null )
            LOG.trace( "No cached value for {}", key );
        else
            LOG.trace( "Returning cached value for {}", key );

        return element;
    }

    /**
     * Forget the result of one invocation.
     *
     * @param invocation the invocation
     */
    public void forget( MethodInvocation invocation ) {
        getCache().remove( getKey( invocation, ChannelsUser.contextUri() ) );
    }

    /**
     * Forget all cached results.
     */
    public void forgetAll() {
        LOG.info( "***Clearing cache " + getCacheKey() );
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

    private static String getKey( MethodInvocation methodInvocation, String contextUri ) {
        String targetMethodName = methodInvocation.getMethod().getName();

        Object[] methodArgs = methodInvocation.getArguments();
        if ( methodArgs == null )
            return targetMethodName;
        else {
            StringBuilder key = new StringBuilder( targetMethodName );
            key.append( '(' );
            for ( int i = 0; i < methodArgs.length; i++ ) {
                if ( i != 0 )
                    key.append( ',' );
                key.append( argumentToString( methodArgs[i] ) );
            }

            // Add plan uri to key
            key.append( ") in " );
            key.append( contextUri );
            return key.toString();
        }
    }

    private static String argumentToString( Object arg ) {
        if ( arg instanceof Identifiable ) {
            StringBuilder sb = new StringBuilder();
            sb.append( String.valueOf( arg ) );
            sb.append( '[' );
            sb.append( ( (Identifiable) arg ).getId() );
            sb.append( ']' );
            return sb.toString();
        } else
            return String.valueOf( arg );
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
