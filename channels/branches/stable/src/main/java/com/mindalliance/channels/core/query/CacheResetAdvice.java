/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.core.query;

import org.springframework.aop.AfterReturningAdvice;

import java.lang.reflect.Method;

/**
 * Clear the cache after invocation.
 */
public class CacheResetAdvice implements AfterReturningAdvice {

    /** The controlled cache. */
    private ResultCache cache;

    public CacheResetAdvice() {
    }

    /**
     * Callback after a given method successfully returned.
     *
     * @param   returnValue  the value returned by the method, if any
     * @param   method       method being invoked
     * @param   args         arguments to the method
     * @param   target       target of the method invocation. May be {@code null}.
     *
     * @throws  Throwable  if this object wishes to abort the call. Any exception thrown will be returned to the caller
     *                     if it's allowed by the method signature. Otherwise the exception will be wrapped as a runtime
     *                     exception.
     */
    @Override
    public void afterReturning( Object returnValue, Method method, Object[] args, Object target ) throws Throwable {
        cache.forgetAll();
    }


    /**
     * Set the value of the cache property.
     *
     * @param  cache  the new value
     */
    public void setCache( ResultCache cache ) {
        this.cache = cache;
    }
}
