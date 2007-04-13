/**
 * 
 */
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
 * @author dfeeney
 *
 */
public abstract class AbstractZkTest extends TestCase implements Richlet {
	protected Server server;
	protected Selenium  selenium;
	


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
        deployer.setWebAppDir("src/test");
        
        deployer.setContexts(contexts);
        server.addLifeCycle(deployer);
        server.start();

        selenium = new DefaultSelenium("localhost",
                4444, "*firefox", "http://localhost:4000/webapp/zk/test");
        selenium.start();

        selenium.open("http://localhost:4000/webapp/zk/test");

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
