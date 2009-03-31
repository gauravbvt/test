package com.mindalliance.channels.export;

import com.mindalliance.channels.AbstractChannelsTest;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.DataQueryObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Dec 16, 2008
 * Time: 4:23:11 PM
 */
public class TestExportImport extends AbstractChannelsTest {

    private List<String> scenarioNames;
    private DataQueryObject dqo;


    @Override
    protected void setUp() {
        super.setUp();
        dqo = project.getDqo();
        scenarioNames = new ArrayList<String>();
        for ( Scenario scenario : project.getDqo().list( Scenario.class ) ) {
            scenarioNames.add( scenario.getName() );
        }
    }

    public void testExportImportScenario() {
        Map<String,String> exported0 = new HashMap<String,String>();
        Map<String,String> exported1 = new HashMap<String,String>();
        Map<String,String> exported2 = new HashMap<String,String>();
        // allow removal of all named scenarios by creating an empty one
        dqo.createScenario();
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
            Scenario scenario = dqo.findScenario( name );
            out = new ByteArrayOutputStream();
            project.getExporter().exportScenario( scenario, out );
            String xml = out.toString();
            // System.out.println( xml );
            exported.put(name,xml);
            assertTrue( xml.length() > 0 );
        }
    }

    private void removeAll() throws Exception {
        for ( String name : scenarioNames ) {
            dqo.remove( dqo.findScenario( name ) );
        }
    }

    private void importAll(Map<String,String> exported) throws Exception {
        for ( String name : scenarioNames ) {
            String xml = exported.get( name );
            ByteArrayInputStream in = new ByteArrayInputStream( xml.getBytes() );
            Scenario scenario = project.getImporter().importScenario( in );
            assertTrue(name.equals(scenario.getName()));
        }
    }
}
