// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.


package com.mindalliance.channels.ui.editor;

import java.util.HashMap;
import java.util.Map;

import com.mindalliance.channels.data.elements.assertions.Assertion;
import com.mindalliance.channels.data.elements.assertions.Known;
import com.mindalliance.channels.data.elements.assertions.NeedsToKnow;
import com.mindalliance.channels.data.elements.scenario.Agent;
import com.mindalliance.channels.data.elements.scenario.RoleAgent;


/**
 * @author <a href="mailto:dfeeney@mind-alliance.com">dfeeney</a>
 * @version $Revision:$
 */
public class InterfaceHelper {
    private static Map<Class, Class[]> interfaceMap;
    
    
    
    public static Class[] retrieveTypes(Class inter) {
        if (interfaceMap == null) {
            initializeMap();
        }
        Class[] types = interfaceMap.get( inter );
        if (types == null) {
            types = new Class[0];
            interfaceMap.put( inter, types );
        }
        return interfaceMap.get( inter );
    }
    /**
     * Initializes the map of interfaces to concrete implementations.  This should 
     * be replaced with a classpath trawler or something less lame.
     */
    private static void initializeMap() {
        if (interfaceMap == null) {
           interfaceMap = new HashMap<Class, Class[]>();
        }
        interfaceMap.put( Agent.class, new Class[] {RoleAgent.class} );
        interfaceMap.put( Assertion.class, new Class[] {NeedsToKnow.class, Known.class} );
    }
    
    
}
