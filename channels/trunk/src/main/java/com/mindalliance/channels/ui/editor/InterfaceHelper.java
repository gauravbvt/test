// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.


package com.mindalliance.channels.ui.editor;

import java.io.File;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sun.misc.Launcher;


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
//            types = new Class[0];
            types = find(inter);
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
//        interfaceMap.put( Agent.class, new Class[] {RoleAgent.class} );
//        interfaceMap.put( Assertion.class, new Class[] {NeedsToKnow.class, Known.class} );
    }
    
    public static List<Class> find(String pckgname, Class inter) {
        List<Class> results = new ArrayList<Class>();
        // Code from JWhich
        // ======
        // Translate the package name into an absolute path
        String name = new String(pckgname);
        if (!name.startsWith("/")) {
            name = "/" + name;
        }        
        name = name.replace('.','/');
        
        // Get a File object for the package
        URL url = inter.getClassLoader().getResource(name);
        if (url != null) {
            File directory = new File(url.getFile());
            // New code
            // ======
            if (directory.exists()) {
                // Get the list of the files contained in the package
                String [] files = directory.list();
                for (int i=0;i<files.length;i++) {
                     
                    // we are only interested in .class files
                    if (files[i].endsWith(".class")) {
                        // removes the .class extension
                        String classname = files[i].substring(0,files[i].length()-6);
                        try {
                            // Try to create an instance of the object
                            Class c = Class.forName(pckgname+"."+classname);
                            if (inter.isAssignableFrom( c )
                                    && !Modifier.isAbstract( c.getModifiers())
                                         ) {
                                // TODO check for default/GUID constructors
                                results.add( c );
                            }
                        } catch (ClassNotFoundException cnfex) {
                            System.err.println(cnfex);
                        } 
                    }
                }
            }
        }
        return results;
    }
    public static Class[] find(Class inter) {
            List<Class> results = new ArrayList<Class>();
            Package [] pcks = Package.getPackages();
            for (int i=0;i<pcks.length;i++) {
                results.addAll( find(pcks[i].getName(),inter) );
            }
            return results.toArray(new Class[0]);
    }
    
}
