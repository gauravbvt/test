// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.services;

import java.beans.PropertyVetoException;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import org.acegisecurity.annotation.Secured;
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

import com.mindalliance.channels.User;
import com.mindalliance.channels.UserExistsException;
import com.mindalliance.channels.data.user.UserImpl;
import com.mindalliance.channels.services.base.ChannelsServiceImpl;

public class AbstractSecurityTest {

    protected UserImpl admin;
    protected UserImpl user;
    protected ChannelsService channelsService;
    protected RegistryService registryService;

    protected ApplicationContext context;
    
    @Before
    @Secured( "RUN_AS_SYSTEM")
    public void setUp() throws PropertyVetoException, UserExistsException {
        
    	channelsService = new ChannelsServiceImpl();
    	registryService = channelsService.getRegistryService();
    	
        admin       = new UserImpl( "An all-powerful administrator", "admin", "admin", 
                        new String[]{ "ROLE_USER", "ROLE_ADMIN" } );
        user        = new UserImpl( "A plain old user", "user", "user", 
                        new String[]{ "ROLE_USER" } );
        // Initialize the registry
        Set<User> users = new TreeSet<User>();
        users.add(user);
        users.add(admin);
        Set<User> admins = new TreeSet<User>();
        admins.add(admin);
        registryService.setAdministrators( admins );
        registryService.setUsers( users );
        
        StaticWebApplicationContext ctx = new StaticWebApplicationContext();
        XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader( ctx );
        xmlReader.loadBeanDefinitions(
                new FileSystemResource( "src/main/webapp/WEB-INF/applicationContext-acegi.xml" ) );        
        xmlReader.loadBeanDefinitions(
                new FileSystemResource( "src/main/webapp/WEB-INF/applicationContext-auth.xml" ) );        

        // TODO make this work
        // ctx.refresh();
        
        context = ctx;
    }

}
