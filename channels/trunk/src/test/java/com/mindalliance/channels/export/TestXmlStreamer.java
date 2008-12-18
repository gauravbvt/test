package com.mindalliance.channels.export;

import com.mindalliance.channels.export.xml.XmlStreamer;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.AbstractChannelsTest;
import com.mindalliance.channels.Dao;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Dec 16, 2008
 * Time: 4:23:11 PM
 */
public class TestXmlStreamer extends AbstractChannelsTest {

    private XmlStreamer xmlStreamer;
    private List<String> scenarioNames;
    private Dao dao;


    protected void setUp() {
        super.setUp();
        dao = project.getDao();
        xmlStreamer = new XmlStreamer();
        scenarioNames = new ArrayList<String>();
        Iterator<Scenario> scenarios = project.getDao().scenarios();
        while ( scenarios.hasNext() ) {
            scenarioNames.add( scenarios.next().getName() );
        }
    }

    public void testExportImportScenario() {
        Map<String,String> exported = new HashMap<String,String>();
        // Export all scenarios
        try {
            for ( String name : scenarioNames ) {
                ByteArrayOutputStream out;
                Scenario scenario = dao.findScenario( name );
                out = new ByteArrayOutputStream();
                xmlStreamer.exportScenario( scenario, out );
                String xml = out.toString();
                System.out.println( xml );
                exported.put(name,xml);
                assertTrue( xml.length() > 0 );
            }
            // remove all scenarios
            for ( String name : scenarioNames ) {
                Scenario scenario = dao.findScenario( name );
                dao.removeScenario( scenario );
            }
            // Import all scenarios
            for ( String name : scenarioNames ) {
                String xml = exported.get( name );
                ByteArrayInputStream in = new ByteArrayInputStream( xml.getBytes() );
                Scenario scenario = xmlStreamer.importScenario( in );
                assertTrue(name.equals(scenario.getName()));
            }
        } catch ( Exception e ) {
            e.printStackTrace();
            fail( e.toString() );
        }
    }
}
