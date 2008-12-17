package com.mindalliance.channels.export;

import com.mindalliance.channels.export.xml.XmlExporter;
import com.mindalliance.channels.export.xml.XmlImporter;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.AbstractChannelsTest;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Dec 16, 2008
 * Time: 4:23:11 PM
 */
public class TestXmlExporter extends AbstractChannelsTest {

    private XmlExporter exporter;
    private XmlImporter importer;
    private List<Long> scenarioIds;


    protected void setUp() {
        super.setUp();
        exporter = new XmlExporter();
        importer = new XmlImporter();
        scenarioIds = new ArrayList<Long>();
        Iterator<Scenario> scenarios = project.getDao().scenarios();
        while ( scenarios.hasNext() ) {
            scenarioIds.add( scenarios.next().getId() );
        }
    }

    public void testExportScenario() {
        for ( long id : scenarioIds ) {
            ByteArrayOutputStream out;
            try {
                Scenario scenario = project.getDao().findScenario( id );
                out = new ByteArrayOutputStream();
                exporter.exportScenario( scenario, out );
                String xml = out.toString();
                assertTrue( xml.length() > 0 );
                ByteArrayInputStream in = new ByteArrayInputStream( xml.getBytes() );
                Scenario scenario1 = importer.importScenario( in );
                assertTrue( scenario1.getName().equals( scenario.getName() ) );
                assertTrue( scenario1.getId() != scenario.getId() );
            } catch ( Exception e ) {
                fail( e.toString() );
            }
        }
    }
}
