// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.services;

import java.beans.PropertyVetoException;

import org.acegisecurity.annotation.Secured;
import org.junit.Before;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.context.support.StaticWebApplicationContext;

import com.mindalliance.channels.Channels;
import com.mindalliance.channels.UserExistsException;
import com.mindalliance.channels.data.user.UserImpl;

public class AbstractSecurityTest {

    protected UserImpl admin;
    protected UserImpl user;
    protected SystemService systemService;
    protected RegistryService registryService;

    protected ApplicationContext context;

    @Before
    @Secured( "RUN_AS_SYSTEM")
    public void setUp() throws PropertyVetoException, UserExistsException {

        systemService = Channels.getSystemService();
        registryService = systemService.getRegistryService();
        if ( !registryService.isUserNameTaken( "admin" ) ) {
            registryService.registerAdministrator(
                    "An all-powerful administrator", "admin", "admin" );
        }
        if ( !registryService.isUserNameTaken( "user" ) ) {
            registryService.registerUser( "A plain old user", "user", "user" );
        }

        StaticWebApplicationContext ctx = new StaticWebApplicationContext();
        XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader( ctx );
        xmlReader.loadBeanDefinitions( new FileSystemResource(
                "src/main/webapp/WEB-INF/applicationContext-acegi.xml" ) );
        xmlReader.loadBeanDefinitions( new FileSystemResource(
                "src/main/webapp/WEB-INF/applicationContext-auth.xml" ) );

        // TODO make this work
        // ctx.refresh();

        context = ctx;
    }

}
