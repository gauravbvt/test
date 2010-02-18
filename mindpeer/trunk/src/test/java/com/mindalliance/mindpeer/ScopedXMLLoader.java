// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.mindpeer;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.test.context.support.GenericXmlContextLoader;

/**
 * Spring tweak for making sure session and request beans get loaded in tests.
 */
public class ScopedXMLLoader  extends GenericXmlContextLoader {

    /**
     * Create a new ScopedXMLLoader instance.
     */
    public ScopedXMLLoader() {
    }

    /**
     * Add session and request scopes.
     * @param beanFactory the bean factory
     */
    @Override
    protected void customizeBeanFactory( DefaultListableBeanFactory beanFactory) {
//        beanFactory.registerScope( "request", new RequestScope() );
//        beanFactory.registerScope( "session", new SessionScope() );

        super.customizeBeanFactory( beanFactory );
    }
}
