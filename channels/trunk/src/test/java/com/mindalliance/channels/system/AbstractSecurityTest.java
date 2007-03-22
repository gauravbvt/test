// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.system;

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
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.context.support.StaticWebApplicationContext;

import com.mindalliance.channels.UserExistsException;
import com.mindalliance.channels.project.ProjectImpl;

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
    
    protected SystemImpl system;
    protected ProjectImpl project;
    protected Organization organization;
    protected UserImpl admin;
    protected UserImpl user;
    protected UserImpl manager;
    protected UserImpl participant;
    protected UserImpl liaison;
    
    protected ApplicationContext context;
    
    @Before
    public void setUp() throws PropertyVetoException, UserExistsException {
        
        admin       = new UserImpl( "Administrator", "admin", "admin", 
                        new String[]{ "ROLE_USER", "ROLE_ADMIN" } );
        user        = new UserImpl( "Joe User", "user", "user", 
                        new String[]{ "ROLE_USER" } );
        manager     = new UserImpl( "Ms Management", "manager", "manager", 
                        new String[]{ "ROLE_USER" } );
        participant = new UserImpl( "Participant", "participant", "participant", 
                        new String[]{ "ROLE_USER" } );
        liaison     = new UserImpl( "Liaison", "liaison", "liaison", 
                        new String[]{ "ROLE_USER" } );
        
        
        project = new ProjectImpl( "The Project" );
        project.addManager( manager );
        project.addParticipant( participant );
        
        organization = new Organization( "The Agency" );
        organization.addLiaision( liaison );

        StaticWebApplicationContext ctx = new StaticWebApplicationContext();
        ctx.registerSingleton( "system", SystemImpl.class );

        system = (SystemImpl) ctx.getBean( "system" );
        system.addAdministrator( admin );
        system.addUser( user );
        system.addUser( manager );
        system.addUser( participant );
        system.addUser( liaison );
        system.addProject( project );
        system.addOrganization( organization );
                
        XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader( ctx );
        xmlReader.loadBeanDefinitions(
                new FileSystemResource( "src/main/webapp/WEB-INF/applicationContext-acegi.xml" ) );        
        xmlReader.loadBeanDefinitions(
                new FileSystemResource( "src/main/webapp/WEB-INF/applicationContext-auth.xml" ) );        

        // TODO make this work
        ctx.refresh();
        
        context = ctx;
    }

    protected void login( String user, String password ) {

        DaoAuthenticationProvider daoAuthenticationProvider =
            new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService( this.system );

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
