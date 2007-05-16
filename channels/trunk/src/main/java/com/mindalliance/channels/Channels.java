// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WebApp;

import com.mindalliance.channels.data.elements.ElementFactory;
import com.mindalliance.channels.data.system.System;
import com.mindalliance.channels.services.SystemService;
import com.mindalliance.channels.services.base.SystemServiceImpl;
import com.mindalliance.channels.util.GUIDFactory;
import com.mindalliance.channels.util.GUIDFactoryImpl;

/**
 * Main application class. Retrieved from application scope. Created if needed.
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
public class Channels {
    
    static public final String SYSTEM_SERVICE = "SystemService";
    static private SystemService SingletonSystemService = null; // TODO - remove - used only for testing outside of a Web app
    public static ElementFactory elementFactory = null;
    public static GUIDFactory guidFactory = null;
    
    static public ElementFactory getElementFactory() {
        if (elementFactory == null) {
            elementFactory = new ElementFactory();
            elementFactory.setGuidFactory( getGUIDFactory() );
        }
        return elementFactory;
    }

    static public GUIDFactory getGUIDFactory() {
        if (guidFactory == null) {
            guidFactory = new GUIDFactoryImpl("Channels");
        }
        return guidFactory;
    }
    
    static public SystemService getSystemService() {
        SystemService systemService = null;
        WebApp webApp = null;
        try {
            webApp = Executions.getCurrent().getDesktop().getWebApp();
        }
        catch (NullPointerException e) {} // TODO - ignore exception for now
        if (webApp != null) {
            systemService = (SystemService)webApp.getAttribute(SYSTEM_SERVICE);
        }
        else { // for testing
            systemService = SingletonSystemService;
        }
        if (systemService == null) {
            systemService = new SystemServiceImpl();
            systemService.setSystem(new System());
           if (webApp != null) {
                synchronized(webApp) { webApp.setAttribute(SYSTEM_SERVICE, systemService); }
            }
            else {
                SingletonSystemService = systemService;
            }
        }
        return systemService;
    }
    
}
