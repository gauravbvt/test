package com.mindalliance.channels;

import java.util.Iterator;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.context.support.StaticWebApplicationContext;

import com.mindalliance.channels.User;
import com.mindalliance.channels.data.frames.PortfolioService;
import com.mindalliance.channels.data.frames.Project;
import com.mindalliance.channels.data.models.Scenario;
import com.mindalliance.channels.data.system.RegistryService;
import com.mindalliance.channels.data.system.SystemService;

/**
 * A Unit Test for exploring the new data model structure as
 * expressed in the applicationContext.xml consumed by the
 * Web application.
 *
 * @author brian
 *
 */
public class ApplicationContextTest {

	private ApplicationContext ctx;

	@Before
	public void setUp() {
        ctx = new StaticWebApplicationContext();
        XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader( (StaticWebApplicationContext)ctx );
        xmlReader.loadBeanDefinitions(
                new FileSystemResource( "src/main/webapp/WEB-INF/applicationContext.xml" ) );
	}

	@Test
	public void testBasic() {
		SystemService ss = (SystemService) ctx.getBean("systemservice");
		Assert.assertNotNull("Got a DirectoryService", ss.getDirectoryService());
		Assert.assertNotNull("Got a RegistryService", ss.getRegistryService());
		Assert.assertNotNull("Got a PortfolioService", ss.getPortfolioService());
		Assert.assertNotNull("Got a HistoryService", ss.getHistoryService());
		Assert.assertNotNull("Got a LibraryService", ss.getLibraryService());

        RegistryService r = ss.getRegistryService();
        Set<User> admins = r.getAdministrators();
        Iterator<User> adminItor = admins.iterator();

        java.lang.System.out.println("Administrators:");
        java.lang.System.out.println("---------------");
        while(adminItor.hasNext()) {
        	java.lang.System.out.println(adminItor.next().getName());
        }

        Set<User> users = r.getUsers();
        Iterator<User> userItor = users.iterator();
        java.lang.System.out.println("Users:");
        java.lang.System.out.println("------");
        while(userItor.hasNext()) {
        	java.lang.System.out.println(userItor.next().getName());
        }

        PortfolioService p = ss.getPortfolioService();

        Set<Project> projs = p.getProjects();
        Iterator<Project> pItor = projs.iterator();
        while(pItor.hasNext()) {
        	Project proj = pItor.next();
        	java.lang.System.out.println("Project: " + proj.getName());
        	Set<Scenario> scenarios = proj.getScenarios();
        	Iterator<Scenario> modelsItor = scenarios.iterator();

        	while(modelsItor.hasNext()) {
        		java.lang.System.out.println("Scenario: " + modelsItor.next().getName());
        	}
        }
	}
}
