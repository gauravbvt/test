// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.support;

/**
 * Weaving for @Secured() methods.
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision:$
 */

import org.acegisecurity.annotation.Secured;
import org.acegisecurity.intercept.method.aspectj.AspectJSecurityInterceptor;
import org.acegisecurity.intercept.method.aspectj.AspectJCallback;
import org.springframework.beans.factory.InitializingBean;

public aspect SystemSecurity implements InitializingBean {

  private AspectJSecurityInterceptor securityInterceptor;

  pointcut securedMethods():
      execution( public * *(..) ) 
      && @annotation( Secured )
      && !within( SystemSecurity )
      ;
  
  // ----------------------------------
  Object around(): securedMethods() {
      
    if ( this.securityInterceptor != null ) {
            AspectJCallback callback = new AspectJCallback() {
                public Object proceedWithObject() {
                    return proceed();
                } };
                
            return this.securityInterceptor.invoke( thisJoinPoint, callback );
        }
        else
            return proceed();
  }

  // ----------------------------------
  public AspectJSecurityInterceptor getSecurityInterceptor() {
    return securityInterceptor;
  }

  public void setSecurityInterceptor( AspectJSecurityInterceptor securityInterceptor ) {
    this.securityInterceptor = securityInterceptor;
  }

  public void afterPropertiesSet() throws Exception {
    if ( this.securityInterceptor == null )
      throw new IllegalArgumentException( "securityInterceptor required" );
  }
}
