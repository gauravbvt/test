package com.mindalliance.channels.core.query;

import net.sf.ehcache.Element;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/** Dynamic cache for method results. */
public class CacheAdvice implements MethodInterceptor {

    /** The cached results. */
    private ResultCache cache;

    public CacheAdvice() {
    }

    /**
     * Called for every advised methods.
     *
     * @param invocation the particular invocation
     * @return the normal or cached result of the invocation
     * @throws Throwable on errors
     */
    @Override
    public Object invoke( MethodInvocation invocation ) throws Throwable {
        Object methodReturn;

        Element cacheElement = cache.getCached( invocation );
        if ( cacheElement == null ) {
            methodReturn = invocation.proceed();
            cache.cache( invocation, methodReturn );
        } else
            methodReturn = cacheElement.getValue();

        return methodReturn;
    }

    public ResultCache getCache() {
        return cache;
    }

    public void setCache( ResultCache cache ) {
        this.cache = cache;
    }
}