package com.mindalliance.channels.query;

import net.sf.ehcache.Element;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * Dynamic cache for method results.
 */
public class CacheAdvice implements MethodInterceptor {

    /** The cached results. */
    private ResultCache cache;

    public CacheAdvice() {
    }

    /**
     * Called for every advised methods.
     *
     * @param methodInvocation the particular invocation
     * @return the normal or cached result of the invocation
     * @throws Throwable on errors
     */
    public Object invoke( MethodInvocation methodInvocation ) throws Throwable {
        Object methodReturn;

        Element cacheElement = cache.getCached( methodInvocation );
        if ( cacheElement == null ) {
            methodReturn = methodInvocation.proceed();
            cache.cache( methodInvocation, methodReturn );
        } else {
            methodReturn = cacheElement.getValue();
        }

        return methodReturn;
    }

    public ResultCache getCache() {
        return cache;
    }

    public void setCache( ResultCache cache ) {
        this.cache = cache;
    }
}