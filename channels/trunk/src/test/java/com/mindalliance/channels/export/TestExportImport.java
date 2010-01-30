package com.mindalliance.channels.export;

import com.mindalliance.channels.AbstractChannelsTest;
import com.mindalliance.channels.Exporter;
import com.mindalliance.channels.Importer;
import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.model.Segment;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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

    private List<String> segmentNames;
    private QueryService queryService;


    @Override
    protected void setUp() throws IOException {
        super.setUp();
        queryService = app.getQueryService();
        segmentNames = new ArrayList<String>();
        for ( Segment segment : app.getQueryService().list( Segment.class ) ) {
            segmentNames.add( segment.getName() );
        }
    }

    public void testExportImportSegment() throws Exception {
        Map<String,String> exported0 = new HashMap<String,String>();
        Map<String,String> exported1 = new HashMap<String,String>();
        Map<String,String> exported2 = new HashMap<String,String>();
        // allow removal of all named segments by creating an empty one
        queryService.createSegment();
        // Export all named segments

        exportAll(exported0);
        removeAll();
        importAll(exported0);
        // re-export
        exportAll(exported1);
        // Import in reverse order
        removeAll();
        Collections.reverse( segmentNames );
        importAll(exported0);
        // re-export
        exportAll(exported2);
        // Make sure all xml serializations are similar
        for (String name : segmentNames ) {
            // export vs export-import-export
            compare(exported0.get(name), exported1.get(name));
            // export vs export-reverse import-export
            compare(exported1.get(name), exported2.get(name));
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
        for ( String name : segmentNames ) {
            Segment segment = queryService.findSegment( name );
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImportExportFactory exportFactory = app.getImportExportFactory();
            Exporter exporter = exportFactory.createExporter( queryService, plan );
            exporter.export( segment, out );
            String xml = out.toString();
            // System.out.println( xml );
            exported.put(name,xml);
            assertTrue( xml.length() > 0 );
        }
    }

    private void removeAll() throws Exception {
        for ( String name : segmentNames ) {
            queryService.remove( queryService.findSegment( name ) );
        }
    }

    private void importAll(Map<String,String> exported) throws Exception {
        for ( String name : segmentNames ) {
            String xml = exported.get( name );
            ByteArrayInputStream in = new ByteArrayInputStream( xml.getBytes() );
            Importer importer = app.getImportExportFactory().createImporter( queryService, plan );
            Segment segment = importer.importSegment( in );
            assertTrue(name.equals(segment.getName()));
        }
    }
}
