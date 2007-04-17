package com.beanview.zk;

import junit.framework.TestCase;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.deployer.WebAppDeployer;
import org.mortbay.jetty.handler.ContextHandlerCollection;
import org.mortbay.jetty.handler.DefaultHandler;
import org.mortbay.jetty.handler.HandlerCollection;
import org.mortbay.jetty.handler.RequestLogHandler;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Richlet;
import org.zkoss.zk.ui.RichletConfig;
import org.zkoss.zk.ui.metainfo.LanguageDefinition;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;

/**
 * Base class for testing ZK components with JUnit.  The setUp() method starts an embedded jetty server with a 
 * basic ZK application that serves as a proxy for the service that sub-classes provide. Since the tested service and the 
 * tests themselves will be running in separate instances, subclasses should maintain the components statically.
 * 
 * <p>Once the service has been started, a session is established using selenium.  This should be used to model UI events.  The 
 * selenium instance expects that a selenium server is running externally.  It also assumes that firefox is installed in the default
 * location.
 *
 */
public abstract class AbstractZkTest<T extends Richlet> extends TestCase {
	
	
	protected Server server;
	protected static Selenium  selenium;
	protected T richlet;
    
	protected int port = 4445;
	protected String serviceUrl = "http://localhost:" + port + "/zk/test";
	
	public AbstractZkTest(T richlet) {
		super();
		setRichlet(richlet);
	}
	
	public void setRichlet(T richlet) {
		this.richlet = richlet;
	}
    
	@Override
    protected void setUp() throws Exception
    {
    	
        super.setUp();

        if (server != null) {
        	stopServer();
        }
        server = new Server();
                    
        Connector connector=new SelectChannelConnector();
        connector.setPort(port);
        server.setConnectors(new Connector[]{connector});
         
        HandlerCollection handlers = new HandlerCollection();
        ContextHandlerCollection contexts = new ContextHandlerCollection();
        RequestLogHandler requestLogHandler = new RequestLogHandler();
        handlers.setHandlers(new Handler[]{contexts,new DefaultHandler(),requestLogHandler});
        server.setHandler(handlers);

        WebAppDeployer deployer = new WebAppDeployer();
        deployer.setWebAppDir("src/test/webapp");
        
        deployer.setContexts(contexts);
        server.addLifeCycle(deployer);
        server.start();

    	TestProxyRichlet.setProxied(richlet);
        selenium = new DefaultSelenium("localhost",
                4444, "*firefox", serviceUrl);
        selenium.start();

        selenium.open(serviceUrl);

    }

	
	
    @Override
    protected void tearDown() throws Exception {
    	super.tearDown();
    	stopServer();
    	selenium.stop();
    	selenium = null;
    	Thread.sleep(1000);
    }

    public void stopServer() throws Exception {
    	server.stop();
    	server.join();
    	server = null;
    }
}
