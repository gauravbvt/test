// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.mindpeer;

import org.apache.wicket.protocol.http.servlet.AbortWithWebErrorCodeException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.intercept.aspectj.AspectJAnnotationCallback;
import org.springframework.security.access.intercept.aspectj.AspectJAnnotationSecurityInterceptor;

import javax.servlet.http.HttpServletResponse;

/**
 * Aspect for static weaving of @Secured methods and classes.
 */
@Aspect
public class SecuredAspects implements InitializingBean {

    private static boolean Bypassed;

    private AspectJAnnotationSecurityInterceptor securityInterceptor;

    /**
     * Create a new SecuredAspects instance.
     */
    public SecuredAspects() {
    }

    /**
     * Any method with an @Secured annotation.
     */
    @Pointcut( "execution(* *(..)) && @annotation(org.springframework.security.access.annotation.Secured)" )
    private void securedMethod() {
    }

    /**
     * Any public method of a class or interface with an @Secured annotation.
     */
    @Pointcut( "execution(public * ((@org.springframework.security.access.annotation.Secured *)+).*(..))" )
    private void securedType() {
    }

    /**
     * Throw a "forbidden" web error on security exceptions in web pages.
     * @param jp the join point
     * @return regular result when no errors.
     * @throws Throwable on other exceptions
     */
    @Around( "execution( * org.apache.wicket.markup.html.WebPage+.*(..) ) && @annotation(org.springframework.security.access.annotation.Secured)" )
    public Object reraise( ProceedingJoinPoint jp ) throws Throwable {
        try {
            return jp.proceed();
        } catch ( AccessDeniedException ignore ) {
            throw new AbortWithWebErrorCodeException( HttpServletResponse.SC_FORBIDDEN );
        }
    }

    /**
     * Do the security check for a secured method.
     * Done this way for compile-time weave with AspectJ.
     * @param jp the join point
     * @return regular result when no errors.
     * @throws Throwable on other exceptions
     */
    @Around( "securedMethod() || securedType()" )
    public Object intercept( final ProceedingJoinPoint jp ) throws Throwable {
        return Bypassed || securityInterceptor == null ?
                 jp.proceed()
               : securityInterceptor.invoke( jp, new AspectJAnnotationCallback() {
                   public Object proceedWithObject() throws Throwable {
                       return jp.proceed();
                   }
               } );
    }

    /**
     * Called after all properties have been set.
     * @throws Exception when no security interceptor was provided
     */
    public void afterPropertiesSet() throws Exception {
        if ( securityInterceptor == null )
            throw new IllegalArgumentException( "securityInterceptor required" );
    }

    /**
     * Sets the securityInterceptor of this aspect.
     * @param interceptor the new securityInterceptor value.
     *
     */
    public void setSecurityInterceptor( AspectJAnnotationSecurityInterceptor interceptor ) {
        securityInterceptor = interceptor;
    }

    /**
     * Forcefully bypass security checked, for tests.
     * @param bypass if checks are to be bypassed
     */
    public static void bypass( boolean bypass ) {
        Bypassed = bypass;
    }
}
