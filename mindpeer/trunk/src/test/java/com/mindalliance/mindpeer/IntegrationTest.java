// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.
package com.mindalliance.mindpeer;

import com.mindalliance.mindpeer.dao.UserDao;
import com.mindalliance.mindpeer.model.User;
import org.apache.wicket.IRequestTarget;
import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;
import org.apache.wicket.request.IRequestCodingStrategy;
import org.apache.wicket.request.IRequestCycleProcessor;
import org.apache.wicket.request.RequestParameters;
import org.apache.wicket.request.target.component.BookmarkablePageRequestTarget;
import org.apache.wicket.util.tester.WicketTester;
import static org.junit.Assert.assertEquals;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManagerFactory;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;

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

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    private IRequestCodingStrategy rcs;

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

        IRequestCycleProcessor rcp = tester.getApplication().getRequestCycleProcessor();
        rcs = rcp.getRequestCodingStrategy();

        parameters = new PageParameters();

        admin = userDao.load( 1L );
        guest = userDao.load( 2L );

        SecuredAspects.bypass( false );
        logout();
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
     * @param username the given user
     * @param password the password
     */
    protected static void login( String username, String password ) {
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication( new UsernamePasswordAuthenticationToken( username, password ) );
    }

    /**
     * ...
     */
    protected static void logout() {
        SecurityContext context = SecurityContextHolder.getContext();
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken( "foo", "bar",
                                                         new ArrayList<GrantedAuthority>() );
        // no granted authorities, ROLE_...
        context.setAuthentication( token );
    }

    /**
     * Sets the beanFactory of this IntegrationTest.
     * @param beanFactory the new beanFactory value.
     *
     * @throws BeansException on errors
     */
    public void setBeanFactory( BeanFactory beanFactory ) throws BeansException {
        this.beanFactory = (DefaultListableBeanFactory) beanFactory;
    }

    @Transactional
    protected void assertRendered( String path, Class<? extends Page> renderedClass )
            throws URISyntaxException {

        assertRendered( tester, path, renderedClass );
    }

    @Transactional
    protected void assertRendered(
            WicketTester actual, String path, Class<? extends Page> renderedClass )
            throws URISyntaxException {
        BookmarkablePageRequestTarget target = getTarget( path );
        actual.startPage( target.getPageClass(), target.getPageParameters() );
        assertEquals( SC_OK, actual.getServletResponse().getCode() );
        actual.assertRenderedPage( renderedClass );
    }

    @Transactional
    @Rollback
    protected void assertErrorRendering( String path, int code ) throws URISyntaxException {
        BookmarkablePageRequestTarget bprt = getTarget( path );
        if ( bprt == null )
            // Fall through to default servlet (files in webapp). Assume not found.
            assertEquals( SC_NOT_FOUND, code );
        else {
            tester.startPage( bprt.getPageClass(), bprt.getPageParameters() );
            assertEquals( code, tester.getServletResponse().getCode() );
        }
    }

    private BookmarkablePageRequestTarget getTarget( String path )
            throws URISyntaxException {

        RequestParameters requestParameters = new RequestParameters();

        if ( path != null && !path.isEmpty() ) {
            URI url = new URI( path );
            requestParameters.setPath( url.getPath() );
            requestParameters.setQueryString( url.getQuery() );
            requestParameters.setParameters( new HashMap<String, Object>() );

            IRequestTarget target = rcs.targetForRequest( requestParameters );
            return (BookmarkablePageRequestTarget) target;
        } else {
            return new BookmarkablePageRequestTarget( wicketApplication.getHomePage() );
        }
    }
}
