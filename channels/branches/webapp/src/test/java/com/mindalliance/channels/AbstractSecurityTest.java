// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels;

import java.beans.PropertyVetoException;
import java.util.Arrays;

import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.context.SecurityContextImpl;
import org.acegisecurity.providers.AuthenticationProvider;
import org.acegisecurity.providers.ProviderManager;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.acegisecurity.providers.dao.DaoAuthenticationProvider;
import org.junit.Before;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.core.io.FileSystemResource;

import com.mindalliance.channels.UserExistsException;
import com.mindalliance.channels.data.frames.Project;
import com.mindalliance.channels.data.profiles.OrganizationImpl;
import com.mindalliance.channels.data.system.SystemService;
import com.mindalliance.channels.data.system.UserImpl;
import com.mindalliance.channels.util.GUIDFactory;

/**
 * A little harness to setup acegi security in unit tests.
 * 
 * <p>It defines a basic system with the following setup:</p>
 * <ul>
 *      <li><b>Project:</b>project.</li>
 *      <li><b>Organization:</b>organization.</li>
 *      <li><b>Users:</b>admin, user, manager, participant, liaison.
 *          Passwords are the same as the username.</li>
 * </ul>
 * 
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision:$
 */
public abstract class AbstractSecurityTest {
    
    protected SystemService system;
    protected Project project;
    protected OrganizationImpl organization;
    protected UserImpl admin;
    protected UserImpl user;
    protected UserImpl manager;
    protected UserImpl guest;
    protected UserImpl liaison;
    protected GUIDFactory guidFactory;
    
    protected ApplicationContext context;
    
    @Before
    public void setUp() throws PropertyVetoException, UserExistsException {
        
        StaticApplicationContext ctx = new StaticApplicationContext();
                
        XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader( ctx );
        xmlReader.loadBeanDefinitions(
                new FileSystemResource( "src/main/webapp/WEB-INF/applicationContext.xml" ) );        
        xmlReader.loadBeanDefinitions(
                new FileSystemResource( "src/main/webapp/WEB-INF/applicationContext-acegi.xml" ) );        
        xmlReader.loadBeanDefinitions(
                new FileSystemResource( "src/main/webapp/WEB-INF/applicationContext-auth.xml" ) );        

        ctx.refresh();

        system = (SystemService) ctx.getBean( "systemservice" );
        admin = (UserImpl) ctx.getBean( "admin" );
        user = (UserImpl) ctx.getBean( "user" );
        manager = (UserImpl) ctx.getBean( "manager" );
        liaison = (UserImpl) ctx.getBean( "liaison" );
        guest = (UserImpl) ctx.getBean( "guest" );
        organization = (OrganizationImpl) ctx.getBean( "organization1" );
        guidFactory = (GUIDFactory) ctx.getBean( "guidFactory" );

        // TODO get a project managed by the manager
        project = system.getPortfolioService().getProjects().iterator().next();
        
        context = ctx;
    }

    protected void login( String user, String password ) {

        DaoAuthenticationProvider daoAuthenticationProvider =
            new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService( this.system.getRegistryService() );

        ProviderManager providerManager = new ProviderManager();
        providerManager.setProviders(
                Arrays.asList( new AuthenticationProvider[]{
                    daoAuthenticationProvider
                } ) );

        // Create and store the Acegi SecureContext into the ContextHolder.
        SecurityContextImpl secureContext = new SecurityContextImpl();
        secureContext.setAuthentication(
                providerManager.doAuthentication(
                        new UsernamePasswordAuthenticationToken(
                                user, password ) ) );
        SecurityContextHolder.setContext( secureContext );
    }
    
    
}
