// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.
package com.mindalliance.mindpeer;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.access.intercept.aspectj.AspectJAnnotationSecurityInterceptor;
import org.aspectj.lang.ProceedingJoinPoint;

/**
 * Coverage test for the @Secured interceptor.
 */
public class TestSecured {

    private SecuredAspects advisor;

    @Mock
    private AspectJAnnotationSecurityInterceptor securityInterceptor;

    @Before
    public void init() {
        MockitoAnnotations.initMocks( this );

        advisor = new SecuredAspects();
    }

    @Test
    public void testAfterPropertiesSet() throws Exception {
        try {
            advisor.afterPropertiesSet();
            Assert.fail();
        } catch ( IllegalArgumentException ignored ) {
            // yay
        }

        advisor.setSecurityInterceptor( securityInterceptor );
        advisor.afterPropertiesSet();
    }

    @Test
    public void testIntercept() throws Throwable {
        SecuredAspects.bypass( false );
        ProceedingJoinPoint jp = Mockito.mock( ProceedingJoinPoint.class );

        advisor.intercept( jp );

        advisor.setSecurityInterceptor( securityInterceptor );
        advisor.intercept( jp );
    }

}
