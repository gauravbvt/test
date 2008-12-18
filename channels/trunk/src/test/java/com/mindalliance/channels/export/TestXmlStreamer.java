package com.mindalliance.channels.export;

import com.mindalliance.channels.AbstractChannelsTest;
import com.mindalliance.channels.Dao;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.export.xml.XmlStreamer;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
        Map<String,String> exported0 = new HashMap<String,String>();
        Map<String,String> exported1 = new HashMap<String,String>();
        Map<String,String> exported2 = new HashMap<String,String>();
        // allow removal of all named scenarios by creating an empty one
        dao.createScenario();
        // Export all named scenarios
        try {
            exportAll(exported0);
            removeAll();
            importAll(exported0);
            // re-export
            exportAll(exported1);
            // Import in reverse order
            removeAll();
            Collections.reverse( scenarioNames );
            importAll(exported0);
            // re-export
            exportAll(exported2);
            // Make sure all xml serializations are similar
            for (String name : scenarioNames) {
                // export vs export-import-export
                compare(exported0.get(name), exported1.get(name));
                // export vs export-reverse import-export
                compare(exported1.get(name), exported2.get(name));
            }

        } catch ( Exception e ) {
            e.printStackTrace();
            fail( e.toString() );
        }
    }

    private void compare(String xml, String otherXml) throws Exception {
        // for now just check if same number of lines
        assertTrue("same number of lines", countLines(xml) == countLines(otherXml));
    }

    private int countLines(String text) throws Exception {
        int count = 0;
        BufferedReader reader = new BufferedReader(new StringReader(text));
        while(reader.readLine() != null) count++;
        return count;
    }

    private void exportAll(Map<String,String> exported) throws Exception {
        for ( String name : scenarioNames ) {
            ByteArrayOutputStream out;
            Scenario scenario = dao.findScenario( name );
            out = new ByteArrayOutputStream();
            xmlStreamer.exportScenario( scenario, out );
            String xml = out.toString();
            // System.out.println( xml );
            exported.put(name,xml);
            assertTrue( xml.length() > 0 );
        }
    }

    private void removeAll() throws Exception {
        for ( String name : scenarioNames ) {
            Scenario scenario = dao.findScenario( name );
            dao.removeScenario( scenario );
        }
    }

    private void importAll(Map<String,String> exported) throws Exception {
        for ( String name : scenarioNames ) {
            String xml = exported.get( name );
            ByteArrayInputStream in = new ByteArrayInputStream( xml.getBytes() );
            Scenario scenario = xmlStreamer.importScenario( in );
            assertTrue(name.equals(scenario.getName()));
        }
    }
}
