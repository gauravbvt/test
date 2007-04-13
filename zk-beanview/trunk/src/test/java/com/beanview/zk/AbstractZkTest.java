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
 * basic ZK application that serves as a proxy for the service that sub-classes implement.  Extending classes should implement
 * the service() method to associate the components to be tested with that service.  Since the tested service and the 
 * tests themselves will be running in separate instances, subclasses should maintain the components statically.
 * 
 * <p>Once the service has been started, a session is established using selenium.  This should be used to model UI events.  The 
 * selenium instance expects that a selenium server is running externally.  It also assumes that firefox is installed in the default
 * location.
 *
 */
public abstract class AbstractZkTest extends TestCase implements Richlet {
	protected static Server server;
	protected static Selenium  selenium;


	public AbstractZkTest() {
    	super();
    	TestProxyRichlet.setProxied(this);
    }
    
	abstract public void service(Page page); 
    
	@Override
    protected void setUp() throws Exception
    {
    	
        super.setUp();

        
        server = new Server();
                    
        Connector connector=new SelectChannelConnector();
        connector.setPort(4000);
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

    	TestProxyRichlet.setProxied(this);
        selenium = new DefaultSelenium("localhost",
                4444, "*firefox", "http://localhost:4000/zk/test");
        selenium.start();

        selenium.open("http://localhost:4000/zk/test");

    }

    @Override
    protected void tearDown() throws Exception {
    	super.tearDown();
    	server.stop();
    	server = null;
    	selenium.stop();
    	selenium = null;
    }
	
    
    /* (non-Javadoc)
	 * @see org.zkoss.zk.ui.Richlet#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.zkoss.zk.ui.Richlet#getLanguageDefinition()
	 */
	public LanguageDefinition getLanguageDefinition() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.zkoss.zk.ui.Richlet#init(org.zkoss.zk.ui.RichletConfig)
	 */
	public void init(RichletConfig arg0) {
		// TODO Auto-generated method stub
		
	}
}
