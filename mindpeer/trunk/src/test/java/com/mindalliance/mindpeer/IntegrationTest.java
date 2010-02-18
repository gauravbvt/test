// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.
package com.mindalliance.mindpeer;

import com.mindalliance.mindpeer.dao.UserDao;
import com.mindalliance.mindpeer.model.User;
import org.apache.wicket.PageParameters;
import org.apache.wicket.protocol.http.MockHttpServletRequest;
import org.apache.wicket.util.tester.WicketTester;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.context.request.RequestScope;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequestEvent;

/**
 * ...
 */
@ContextConfiguration( loader = ScopedXMLLoader.class,
                       locations = { "/applicationContext.xml", "/integratedTestContext.xml" } )
@RunWith( SpringJUnit4ClassRunner.class )
public abstract class IntegrationTest implements BeanFactoryAware {

    @Autowired
    protected WicketApplication wicketApplication;

    @Autowired
    protected JavaMailSender mailSender;

    protected WicketTester tester;

    protected PageParameters parameters;

    @Autowired
    protected UserDao userDao;

    protected User admin;
    protected User guest;

    private DefaultListableBeanFactory beanFactory;

    /**
     * Create a new IntegrationTest instance.
     */
    protected IntegrationTest() {
    }

    /**
     * ...
     */
    @Before
    public void init() {
        MockitoAnnotations.initMocks( this );
        tester = new WicketTester( wicketApplication );
        parameters = new PageParameters();

        admin = userDao.load( 1L );
        guest = userDao.load( 2L );

        SecuredAspects.bypass( false );

        SecurityContext context = SecurityContextHolder.getContext();
        TestingAuthenticationToken auth =
                new TestingAuthenticationToken( new User(), "", "ROLE_ANONYMOUS" );
        context.setAuthentication( auth );

    }

    /**
     * ...
     */
    @Test
    public void testSetup() {
        assertNotNull( wicketApplication );
        assertNotNull( mailSender );
    }

    /**
     * Fake login in of a given user, for security test.
     * @param user the given user
     * @param password the password
     */
    protected static void login( User user, String password ) {
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication( new UsernamePasswordAuthenticationToken( user, password ) );
    }

    /**
     * ...
     */
    protected static void logout() {
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication( null );
    }

    /**
     * ...
     *
     * @param url the given url
     * @param code the given code
     */
    protected void runInRequest( String url, Runnable code ) {
        runInRequest( url, null, null, code );
    }

    /**
     * ...
     *
     * @param url the given url
     * @param user the given user
     * @param password the given password
     * @param code the given code
     */
    protected void runInRequest( String url, User user, String password, Runnable code ) {
        ServletContext context = tester.getServletSession().getServletContext();
        MockHttpServletRequest request = tester.getServletRequest();
        request.setURL( url );

        beanFactory.registerScope( "request", new RequestScope() );

        ServletRequestEvent requestEvent = new ServletRequestEvent( context, request );

        RequestContextListener listener = new RequestContextListener();
        listener.requestInitialized( requestEvent );
        try {
            if ( user != null )
                login( user, password );
            code.run();
        } finally {
            logout();
            listener.requestDestroyed( requestEvent );
        }
    }

    public void setBeanFactory( BeanFactory beanFactory ) throws BeansException {
        this.beanFactory = (DefaultListableBeanFactory) beanFactory;
    }
}
