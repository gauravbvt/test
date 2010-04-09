package com.mindalliance.channels;

import com.mindalliance.channels.dao.FileUserDetailsService;
import com.mindalliance.channels.dao.PlanManager;
import com.mindalliance.channels.dao.User;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.pages.Channels;
import com.mindalliance.channels.query.DefaultQueryService;
import com.mindalliance.channels.command.Commander;
import com.mindalliance.channels.command.LockManager;
import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebRequestCycle;
import org.apache.wicket.request.IRequestCodingStrategy;
import org.apache.wicket.request.IRequestCycleProcessor;
import org.apache.wicket.request.RequestParameters;
import org.apache.wicket.request.target.component.BookmarkablePageRequestTarget;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AbstractContextLoader;
import org.springframework.test.context.support.AbstractTestExecutionListener;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.SessionScope;
import org.springframework.web.context.support.GenericWebApplicationContext;

import javax.servlet.ServletContext;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Dec 3, 2008
 * Time: 8:24:56 PM
 */
@ContextConfiguration(
    loader = AbstractChannelsTest.MyContextLoader.class,
    locations = {
        "file:src/main/webapp/WEB-INF/applicationContext.xml",
            "file:src/main/webapp/WEB-INF/securityConfig.xml",
//            "file:src/main/webapp/WEB-INF/persistenceConfig.xml",
        "testConfig.xml" } )
@TestExecutionListeners( {
        AbstractChannelsTest.ClearDataListener.class,
        AbstractChannelsTest.ReinitContextListener.class,
        DependencyInjectionTestExecutionListener.class
        } )
@RunWith( SpringJUnit4ClassRunner.class )
public abstract class AbstractChannelsTest implements ApplicationContextAware {

    private static final Logger LOG = LoggerFactory.getLogger( AbstractChannelsTest.class );

    @Autowired
    protected Channels wicketApplication;

    @Autowired
    protected DefaultQueryService queryService;

    protected WicketTester tester;

    @Autowired
    private FileUserDetailsService userDetailsService;

    private IRequestCodingStrategy rcs;

    private ConfigurableApplicationContext applicationContext;

    private String userName;

    private String planUri;

    @Autowired
    protected PlanManager planManager;

    //================================================================
    protected AbstractChannelsTest() {
        this( "guest", null );
    }

    protected AbstractChannelsTest( String userName, String planUri ) {
        this.userName = userName;
        this.planUri = planUri;
    }

    @Before
    public void setUp() throws IOException {

        IRequestCycleProcessor rcp = wicketApplication.getRequestCycleProcessor();
        rcs = rcp.getRequestCodingStrategy();

        applicationContext.getBeanFactory().registerScope("session", new SessionScope());

        wicketApplication.setInjector(
                new SpringComponentInjector( wicketApplication, applicationContext ) );

        tester = new WicketTester( wicketApplication ) {
            @Override
            public ServletContext newServletContext( String path ) {
                return ( (WebApplicationContext) applicationContext ).getServletContext();
            }

            @Override
            public WebRequestCycle setupRequestAndResponse( boolean isAjax ) {
                WebRequestCycle result = super.setupRequestAndResponse( isAjax );
                RequestContextHolder.setRequestAttributes(
                        new ServletRequestAttributes( getServletRequest() ) );

                return result;
            }
        };

        tester.setParametersForNextRequest( new HashMap<String, String[]>() );

        if ( userName != null )
            login( userName );

        if ( planUri != null ) {
            List<Plan> planList = planManager.getPlansWithUri( planUri );
            User.current().setPlan( planList.get( 0 ) );
        }
    }

    @After
    public void tearDown() {
        if ( userName != null )
            logout();
        RequestContextHolder.resetRequestAttributes();
    }

    /**
     * Fake login in of a given user, for security test.
     * @param username the given user
     */
    protected void login( String username ) {
        SecurityContext context = SecurityContextHolder.getContext();
        UserDetails details = userDetailsService.loadUserByUsername( username );
        TestingAuthenticationToken auth = new TestingAuthenticationToken(
                details, "",  (List<GrantedAuthority>) details.getAuthorities() );
        auth.setAuthenticated( true );
        context.setAuthentication( auth );
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

    public Commander getCommander() {
        User user = User.current();
        assertNotNull( "No current user", user );
        Plan plan = user.getPlan();
        assertNotNull( "No plan defined for user", plan );
        return wicketApplication.getCommander( plan );
    }

    public LockManager getLockManager() {
        User user = User.current();
        assertNotNull( "No current user", user );
        Plan plan = user.getPlan();
        return wicketApplication.getLockManager( plan );
    }

    protected void assertRendered( String path, Class<? extends Page> renderedClass ) {
        BookmarkablePageRequestTarget target = getTarget( path );
        tester.startPage( target.getPageClass(), target.getPageParameters() );
        assertEquals( SC_OK, tester.getServletResponse().getCode() );
        tester.assertRenderedPage( renderedClass );
        tester.assertNoErrorMessage();
    }

    protected void assertErrorRendering( String path, int code ) {
        BookmarkablePageRequestTarget bprt = getTarget( path );
        if ( bprt == null )
            // Fall through to default servlet (files in webapp). Assume not found.
            assertEquals( SC_NOT_FOUND, code );
        else {
            tester.startPage( bprt.getPageClass(), bprt.getPageParameters() );
            assertEquals( code, tester.getServletResponse().getCode() );
        }
    }

    /**
     * Clear all persisted data.
     */
    protected static void clearData() {
        LOG.debug( "Clearing all saved data" );
        File path = new File( "target/channel-test-data" );
        deleteDirectory( path );
        path.mkdirs();
    }

    protected static void installSamples() {
        LOG.debug( "Installing samples" );
        try {
            copyFiles( new File( "src/main/samples" ), new File( "target/channel-test-data" ) );
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }
    }

    /**
     * This function will copy files or directories from one location to another.
     * note that the source and the destination must be mutually exclusive. This
     * function can not be used to copy a directory to a sub directory of itself.
     * The function will also have problems if the destination files already exist.
     * @param src A File object that represents the source for the copy
     * @param dest A File object that represents the destination for the copy.
     * @throws IOException if unable to copy.
     */
    private static void copyFiles( File src, File dest ) throws IOException {

        if ( src.isDirectory() ) {
            if ( dest.exists() || dest.mkdirs() )
                for ( String f : src.list() )
                    copyFiles( new File( src, f ), new File( dest, f ) );

            else
                throw new IOException( "Could not create directory: " + dest.getAbsolutePath() );

        } else {
            FileInputStream in = null;
            FileOutputStream out = null;
            try {
                in = new FileInputStream( src );
                out = new FileOutputStream( dest );
                byte[] buffer = new byte[4096];

                int bytesRead = in.read( buffer );
                while ( bytesRead >= 0 ) {
                    out.write( buffer, 0, bytesRead );
                    bytesRead = in.read( buffer );
                }
            } finally { //Ensure that the files are closed (if they were open).
                if ( in != null )
                    in.close();

                if ( out != null )
                    out.close();
            }
        }
    }

    private static void deleteDirectory( File path ) {
        if ( path.exists() ) {
            for ( File file : path.listFiles() )
                if ( file.isDirectory() )
                    deleteDirectory( file );
                else
                    file.delete();

            path.delete();
        }
    }

    private BookmarkablePageRequestTarget getTarget( String path ) {

        if ( path == null || path.isEmpty() )
            return new BookmarkablePageRequestTarget( wicketApplication.getHomePage() );
        try {
            URI url = new URI( path );
            RequestParameters requestParameters = new RequestParameters();
            requestParameters.setPath( url.getPath() );
            requestParameters.setQueryString( url.getQuery() );
            requestParameters.setParameters( new HashMap<String, Object>() );
            return (BookmarkablePageRequestTarget) rcs.targetForRequest( requestParameters );

        } catch ( URISyntaxException e ) {
            // Error in the writing of the test...
            throw new RuntimeException( e );
        }
    }

    public void setApplicationContext( ApplicationContext applicationContext ) {
        this.applicationContext = (ConfigurableApplicationContext) applicationContext;
    }

    //=======================================================
    public static class MyContextLoader extends AbstractContextLoader {

        private ServletContext servletContext =
                new MockServletContext( "src/main/webapp",
                                        new FileSystemResourceLoader() );

        public final ConfigurableApplicationContext loadContext( String... locations ) {
            if ( LOG.isDebugEnabled() ) {
                LOG.debug( "Loading ApplicationContext for locations [ {} ]",
                              StringUtils.arrayToCommaDelimitedString( locations ) );
            }

            GenericWebApplicationContext webContext = new GenericWebApplicationContext();
            servletContext.setAttribute(
                    WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE,
                    webContext );

            webContext.setServletContext( servletContext );

            new XmlBeanDefinitionReader( webContext ).loadBeanDefinitions( locations );
            AnnotationConfigUtils.registerAnnotationConfigProcessors( webContext );
            webContext.refresh();
            webContext.registerShutdownHook();

            return webContext;
        }


        protected String getResourceSuffix() {
            return "-context.xml";
        }
    }

    //=======================================================
    /**
     * Force new test to reload the context *before* test is run.
     */
    public static class ReinitContextListener extends AbstractTestExecutionListener {

        @Override
        public void afterTestMethod( TestContext testContext ) {
            LOG.debug( "Forcing test context reload after test method" );
            reload( testContext );
        }

        private static void reload( TestContext testContext ) {
            testContext.markApplicationContextDirty();
            testContext.setAttribute(
                    DependencyInjectionTestExecutionListener.REINJECT_DEPENDENCIES_ATTRIBUTE,
                    Boolean.TRUE );
        }
    }

    //=======================================================
    /**
     * Clear all data *before* test class is run.
     */
    public static class ClearDataListener extends AbstractTestExecutionListener {

        @Override
        public void beforeTestClass( TestContext testContext ) {
//            CacheManager cacheManager =
//                    (CacheManager) testContext.getApplicationContext().getBean( "cacheManager" );
//            cacheManager.clearAll();

            clearData();
        }
    }

    /**
     * Install samples *before* test class is run.
     */
    public static class InstallSamplesListener extends AbstractTestExecutionListener {

        @Override
        public void beforeTestClass( TestContext testContext ) {
            installSamples();
        }
    }
}
